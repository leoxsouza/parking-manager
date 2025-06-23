package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.model.ParkingSession
import com.leonardo.parkingmanager.model.Sector
import com.leonardo.parkingmanager.model.Spot
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import com.leonardo.parkingmanager.repository.SectorRepository
import com.leonardo.parkingmanager.repository.SpotRepository
import com.leonardo.parkingmanager.service.PricingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Instant
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@ExperimentalCoroutinesApi
class ExitEventStrategyTest {

    private lateinit var parkingSessionRepository: ParkingSessionRepository
    private lateinit var spotRepository: SpotRepository
    private lateinit var sectorRepository: SectorRepository
    private lateinit var pricingService: PricingService
    private lateinit var exitEventStrategy: ExitEventStrategy

    private val testLicensePlate = "ABC1234"
    private val testSpotId = 1L
    private val testSessionId = 100L
    private val testSectorName = "SECTOR_A"
    private val testBasePrice = 10.0
    private val testDynamicPrice = 12.5
    private val testFinalPrice = 15.0
    private val testDurationLimitMinutes = 120

    @BeforeEach
    fun setup() {
        parkingSessionRepository = mock()
        spotRepository = mock()
        sectorRepository = mock()
        pricingService = mock()
        exitEventStrategy = ExitEventStrategy(
            parkingSessionRepository,
            spotRepository,
            sectorRepository,
            pricingService
        )
    }

    @Test
    fun `should have correct event type`() {
        // Given/When/Then
        assert(exitEventStrategy.eventType == EventType.EXIT)
    }

    @Test
    fun `should update parking session with exit time and final price when handling exit event`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val parkedTime = Instant.parse("2025-06-22T22:05:00Z")
        val exitTime = Instant.parse("2025-06-22T23:05:00Z")
        val durationMinutes = 60L

        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = exitTime,
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = parkedTime,
            exitTime = null,
            price = testDynamicPrice,
            spotId = testSpotId
        )

        val spot = Spot(
            id = testSpotId,
            lat = 40.7128,
            lng = -74.0060,
            sectorName = testSectorName,
            occupied = true
        )

        val sector = Sector(
            id = 1L,
            name = testSectorName,
            basePrice = testBasePrice,
            maxCapacity = 10,
            openHour = LocalTime.of(8, 0),
            closeHour = LocalTime.of(20, 0),
            durationLimitMinutes = testDurationLimitMinutes
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(spotRepository.findById(testSpotId)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(sector)
        whenever(pricingService.calculateFinalPrice(testDynamicPrice, durationMinutes, testDurationLimitMinutes))
            .thenReturn(testFinalPrice)

        // When
        exitEventStrategy.handleEvent(dto)

        // Then
        verify(parkingSessionRepository).findByLicensePlateAndExitTimeIsNull(testLicensePlate)
        verify(spotRepository).findById(testSpotId)
        verify(sectorRepository).findByName(testSectorName)
        verify(pricingService).calculateFinalPrice(testDynamicPrice, durationMinutes, testDurationLimitMinutes)

        // Verify session update
        verify(parkingSessionRepository).save(argThat {
            this.id == testSessionId &&
            this.licensePlate == testLicensePlate &&
            this.exitTime == exitTime &&
            this.price == testFinalPrice
        })
    }

    @Test
    fun `should use current time when exit time is not provided`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val parkedTime = Instant.parse("2025-06-22T22:05:00Z")
        val now = Instant.now()

        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = null,
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = parkedTime,
            exitTime = null,
            price = testDynamicPrice,
            spotId = testSpotId
        )

        val spot = Spot(
            id = testSpotId,
            lat = 40.7128,
            lng = -74.0060,
            sectorName = testSectorName,
            occupied = true
        )

        val sector = Sector(
            id = 1L,
            name = testSectorName,
            basePrice = testBasePrice,
            maxCapacity = 10,
            openHour = LocalTime.of(8, 0),
            closeHour = LocalTime.of(20, 0),
            durationLimitMinutes = testDurationLimitMinutes
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(spotRepository.findById(testSpotId)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(sector)
        whenever(pricingService.calculateFinalPrice(eq(testDynamicPrice), any(), eq(testDurationLimitMinutes)))
            .thenReturn(testFinalPrice)

        // When
        exitEventStrategy.handleEvent(dto)

        // Then
        verify(parkingSessionRepository).save(argThat {
            this.exitTime != null &&
            this.exitTime!!.isAfter(parkedTime) &&
            // The exit time should be close to now
            ChronoUnit.SECONDS.between(this.exitTime, now) < 10
        })
    }

    @Test
    fun `should use base price when session price is null`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val parkedTime = Instant.parse("2025-06-22T22:05:00Z")
        val exitTime = Instant.parse("2025-06-22T23:05:00Z")
        val durationMinutes = 60L

        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = exitTime,
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = parkedTime,
            exitTime = null,
            price = null, // No price set
            spotId = testSpotId
        )

        val spot = Spot(
            id = testSpotId,
            lat = 40.7128,
            lng = -74.0060,
            sectorName = testSectorName,
            occupied = true
        )

        val sector = Sector(
            id = 1L,
            name = testSectorName,
            basePrice = testBasePrice,
            maxCapacity = 10,
            openHour = LocalTime.of(8, 0),
            closeHour = LocalTime.of(20, 0),
            durationLimitMinutes = testDurationLimitMinutes
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(spotRepository.findById(testSpotId)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(sector)
        whenever(pricingService.calculateFinalPrice(testBasePrice, durationMinutes, testDurationLimitMinutes))
            .thenReturn(testFinalPrice)

        // When
        exitEventStrategy.handleEvent(dto)

        // Then
        verify(pricingService).calculateFinalPrice(testBasePrice, durationMinutes, testDurationLimitMinutes)
    }

    @Test
    fun `should throw IllegalArgumentException when license plate is blank`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = "",
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = Instant.parse("2025-06-22T23:05:00Z"),
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        // When/Then
        assertThrows<IllegalArgumentException> {
            exitEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when no active parking session is found`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = Instant.parse("2025-06-22T23:05:00Z"),
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(mutableListOf())

        // When/Then
        assertThrows<IllegalStateException> {
            exitEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when parking session has no spot ID`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val parkedTime = Instant.parse("2025-06-22T22:05:00Z")
        val exitTime = Instant.parse("2025-06-22T23:05:00Z")

        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = exitTime,
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = parkedTime,
            exitTime = null,
            price = testDynamicPrice,
            spotId = null // No spot ID
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)

        // When/Then
        assertThrows<IllegalStateException> {
            exitEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when parking session has no parked time`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val exitTime = Instant.parse("2025-06-22T23:05:00Z")

        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = exitTime,
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = null, // No parked time
            exitTime = null,
            price = testDynamicPrice,
            spotId = testSpotId
        )

        val spot = Spot(
            id = testSpotId,
            lat = 40.7128,
            lng = -74.0060,
            sectorName = testSectorName,
            occupied = true
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(spotRepository.findById(testSpotId)).thenReturn(spot)

        // When/Then
        assertThrows<IllegalStateException> {
            exitEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when spot is not found`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val parkedTime = Instant.parse("2025-06-22T22:05:00Z")
        val exitTime = Instant.parse("2025-06-22T23:05:00Z")

        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = exitTime,
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = parkedTime,
            exitTime = null,
            price = testDynamicPrice,
            spotId = testSpotId
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(spotRepository.findById(testSpotId)).thenReturn(null)

        // When/Then
        assertThrows<IllegalStateException> {
            exitEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when sector is not found`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val parkedTime = Instant.parse("2025-06-22T22:05:00Z")
        val exitTime = Instant.parse("2025-06-22T23:05:00Z")

        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = exitTime,
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = parkedTime,
            exitTime = null,
            price = testDynamicPrice,
            spotId = testSpotId
        )

        val spot = Spot(
            id = testSpotId,
            lat = 40.7128,
            lng = -74.0060,
            sectorName = testSectorName,
            occupied = true
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(spotRepository.findById(testSpotId)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(null)

        // When/Then
        assertThrows<IllegalStateException> {
            exitEventStrategy.handleEvent(dto)
        }
    }
}
