package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.exception.SpotAlreadyOccupiedException
import com.leonardo.parkingmanager.model.ParkingSession
import com.leonardo.parkingmanager.model.Sector
import com.leonardo.parkingmanager.model.Spot
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import com.leonardo.parkingmanager.repository.SectorRepository
import com.leonardo.parkingmanager.service.PricingService
import com.leonardo.parkingmanager.service.SpotService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Instant
import java.time.LocalTime

@ExperimentalCoroutinesApi
class ParkedEventStrategyTest {

    private lateinit var parkingSessionRepository: ParkingSessionRepository
    private lateinit var sectorRepository: SectorRepository
    private lateinit var pricingService: PricingService
    private lateinit var spotService: SpotService
    private lateinit var parkedEventStrategy: ParkedEventStrategy

    private val testLicensePlate = "ABC1234"
    private val testLatitude = 40.7128
    private val testLongitude = -74.0060
    private val testSectorName = "SECTOR_A"
    private val testSpotId = 1L
    private val testSessionId = 100L
    private val testBasePrice = 10.0
    private val testCalculatedPrice = 12.5

    @BeforeEach
    fun setup() {
        parkingSessionRepository = mock()
        sectorRepository = mock()
        pricingService = mock()
        spotService = mock()
        parkedEventStrategy = ParkedEventStrategy(
            parkingSessionRepository,
            sectorRepository,
            pricingService,
            spotService
        )
    }

    @Test
    fun `should have correct event type`() {
        // Given/When/Then
        assert(parkedEventStrategy.eventType == EventType.PARKED)
    }

    @Test
    fun `should update parking session and spot when handling valid parked event`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:00:00Z")
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            exitTime = null,
            latitude = testLatitude,
            longitude = testLongitude,
            eventType = EventType.PARKED
        )

        val spot = Spot(
            id = testSpotId,
            lat = testLatitude,
            lng = testLongitude,
            sectorName = testSectorName,
            occupied = false
        )

        val sector = Sector(
            id = 1L,
            name = testSectorName,
            basePrice = testBasePrice,
            maxCapacity = 10,
            openHour = LocalTime.of(8, 0),
            closeHour = LocalTime.of(20, 0),
            durationLimitMinutes = 120
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = entryTime,
            parkedTime = null,
            exitTime = null,
            price = null,
            spotId = null
        )

        val sessions = MutableList(1) { session }

        // Mock dependencies
        whenever(spotService.findSpot(testLatitude, testLongitude)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(sector)
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(parkingSessionRepository.countActiveSessionsBySector(testSectorName)).thenReturn(5)
        whenever(pricingService.calculateDynamicPrice(testBasePrice, 0.5)).thenReturn(testCalculatedPrice)

        // When
        parkedEventStrategy.handleEvent(dto)

        // Then
        verify(spotService).findSpot(testLatitude, testLongitude)
        verify(sectorRepository).findByName(testSectorName)
        verify(parkingSessionRepository).findByLicensePlateAndExitTimeIsNull(testLicensePlate)
        verify(parkingSessionRepository).countActiveSessionsBySector(testSectorName)
        verify(pricingService).calculateDynamicPrice(testBasePrice, 0.5)
        
        // Verify session update
        verify(parkingSessionRepository).save(argThat {
            this.id == testSessionId &&
            this.licensePlate == testLicensePlate &&
            this.spotId == testSpotId &&
            this.price == testCalculatedPrice &&
            this.parkedTime != null
        })
        
        // Verify spot update
        verify(spotService).updateSpot(argThat {
            this.id == testSpotId &&
            this.occupied
        })
    }

    @Test
    fun `should throw IllegalArgumentException when latitude is missing`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = null,
            latitude = null,
            longitude = testLongitude,
            eventType = EventType.PARKED
        )

        // When/Then
        assertThrows<IllegalArgumentException> {
            parkedEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when longitude is missing`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = null,
            latitude = testLatitude,
            longitude = null,
            eventType = EventType.PARKED
        )

        // When/Then
        assertThrows<IllegalArgumentException> {
            parkedEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw SpotAlreadyOccupiedException when spot is already occupied`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = null,
            latitude = testLatitude,
            longitude = testLongitude,
            eventType = EventType.PARKED
        )

        val occupiedSpot = Spot(
            id = testSpotId,
            lat = testLatitude,
            lng = testLongitude,
            sectorName = testSectorName,
            occupied = true
        )

        whenever(spotService.findSpot(testLatitude, testLongitude)).thenReturn(occupiedSpot)

        // When/Then
        assertThrows<SpotAlreadyOccupiedException> {
            parkedEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when sector is not found`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = null,
            latitude = testLatitude,
            longitude = testLongitude,
            eventType = EventType.PARKED
        )

        val spot = Spot(
            id = testSpotId,
            lat = testLatitude,
            lng = testLongitude,
            sectorName = testSectorName,
            occupied = false
        )

        whenever(spotService.findSpot(testLatitude, testLongitude)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(null)

        // When/Then
        assertThrows<IllegalStateException> {
            parkedEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when no active parking session is found`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = null,
            latitude = testLatitude,
            longitude = testLongitude,
            eventType = EventType.PARKED
        )

        val spot = Spot(
            id = testSpotId,
            lat = testLatitude,
            lng = testLongitude,
            sectorName = testSectorName,
            occupied = false
        )

        val sector = Sector(
            id = 1L,
            name = testSectorName,
            basePrice = testBasePrice,
            maxCapacity = 10,
            openHour = LocalTime.of(8, 0),
            closeHour = LocalTime.of(20, 0),
            durationLimitMinutes = 120
        )

        whenever(spotService.findSpot(testLatitude, testLongitude)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(sector)
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(mutableListOf())

        // When/Then
        assertThrows<IllegalStateException> {
            parkedEventStrategy.handleEvent(dto)
        }
    }

    @Test
    fun `should throw IllegalStateException when sector is full`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            exitTime = null,
            latitude = testLatitude,
            longitude = testLongitude,
            eventType = EventType.PARKED
        )

        val spot = Spot(
            id = testSpotId,
            lat = testLatitude,
            lng = testLongitude,
            sectorName = testSectorName,
            occupied = false
        )

        val sector = Sector(
            id = 1L,
            name = testSectorName,
            basePrice = testBasePrice,
            maxCapacity = 10,
            openHour = LocalTime.of(8, 0),
            closeHour = LocalTime.of(20, 0),
            durationLimitMinutes = 120
        )

        val session = ParkingSession(
            id = testSessionId,
            licensePlate = testLicensePlate,
            entryTime = Instant.parse("2025-06-22T22:00:00Z"),
            parkedTime = null,
            exitTime = null,
            price = null,
            spotId = null
        )

        val sessions = MutableList(1) { session }

        whenever(spotService.findSpot(testLatitude, testLongitude)).thenReturn(spot)
        whenever(sectorRepository.findByName(testSectorName)).thenReturn(sector)
        whenever(parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(testLicensePlate))
            .thenReturn(sessions)
        whenever(parkingSessionRepository.countActiveSessionsBySector(testSectorName)).thenReturn(10)

        // When/Then
        assertThrows<IllegalStateException> {
            parkedEventStrategy.handleEvent(dto)
        }
    }
}
