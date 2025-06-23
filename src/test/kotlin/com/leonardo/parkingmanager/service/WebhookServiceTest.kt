package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.time.Instant
import java.util.EnumMap

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class WebhookServiceTest {

    @Mock
    private lateinit var entryEventStrategy: WebhookEventStrategy

    @Mock
    private lateinit var exitEventStrategy: WebhookEventStrategy

    @Mock
    private lateinit var parkedEventStrategy: WebhookEventStrategy

    private lateinit var webhookService: WebhookService

    @BeforeEach
    fun setup() {
        webhookService = WebhookService()

        // Configurar manualmente o mapa de estratégias
        val strategyMap: MutableMap<EventType, WebhookEventStrategy> = EnumMap(EventType::class.java)
        strategyMap[EventType.ENTRY] = entryEventStrategy
        strategyMap[EventType.EXIT] = exitEventStrategy
        strategyMap[EventType.PARKED] = parkedEventStrategy

        ReflectionTestUtils.setField(webhookService, "eventTypeStrategyMap", strategyMap)
    }

    @Test
    fun `should delegate to entry event strategy when event type is ENTRY`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = "ABC1234",
            entryTime = Instant.parse("2025-06-22T22:22:33Z"),
            exitTime = null,
            latitude = null,
            longitude = null,
            eventType = EventType.ENTRY
        )

        // When
        webhookService.handleEvent(dto)

        // Then
        verify(entryEventStrategy).handleEvent(dto)
    }

    @Test
    fun `should delegate to exit event strategy when event type is EXIT`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = "ABC1234",
            entryTime = null,
            exitTime = Instant.parse("2025-06-22T23:22:33Z"),
            latitude = null,
            longitude = null,
            eventType = EventType.EXIT
        )

        // When
        webhookService.handleEvent(dto)

        // Then
        verify(exitEventStrategy).handleEvent(dto)
    }

    @Test
    fun `should delegate to parked event strategy when event type is PARKED`() = runTest {
        // Given
        val dto = WebhookDto(
            licensePlate = "ABC1234",
            entryTime = null,
            exitTime = null,
            latitude = 40.7128,
            longitude = -74.0060,
            eventType = EventType.PARKED
        )

        // When
        webhookService.handleEvent(dto)

        // Then
        verify(parkedEventStrategy).handleEvent(dto)
    }
}
