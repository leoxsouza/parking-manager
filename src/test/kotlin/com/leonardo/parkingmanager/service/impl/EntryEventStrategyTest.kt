package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.model.ParkingSession
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Instant

@ExperimentalCoroutinesApi
class EntryEventStrategyTest {

    private lateinit var parkingSessionRepository: ParkingSessionRepository
    private lateinit var entryEventStrategy: EntryEventStrategy

    @BeforeEach
    fun setup() {
        parkingSessionRepository = mock()
        entryEventStrategy = EntryEventStrategy(parkingSessionRepository)
    }

    @Test
    fun `should save new parking session when handling entry event`() = runTest {
        // Given
        val entryTime = Instant.parse("2025-06-22T22:22:33Z")
        val dto = WebhookDto(
            licensePlate = "ABC1234",
            entryTime = entryTime,
            exitTime = null,
            latitude = null,
            longitude = null,
            eventType = EventType.ENTRY
        )

        whenever(parkingSessionRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<ParkingSession>(0)
        }

        // When
        entryEventStrategy.handleEvent(dto)

        // Then
        verify(parkingSessionRepository).save(argThat {
            this.licensePlate == "ABC1234" && this.entryTime == entryTime
        })
    }

    @Test
    fun `should handle event with correct event type`() {
        // Given/When/Then
        assert(entryEventStrategy.eventType == EventType.ENTRY)
    }
}
