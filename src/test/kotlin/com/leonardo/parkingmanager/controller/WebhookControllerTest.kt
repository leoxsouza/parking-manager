package com.leonardo.parkingmanager.controller

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.service.WebhookService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant

@ExperimentalCoroutinesApi
@WebFluxTest(WebhookController::class)
class WebhookControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var webhookService: WebhookService

    @Test
    fun `should return 200 OK when webhook is processed successfully`() = runTest {
        // Given
        val webhookDto = WebhookDto(
            licensePlate = "ABC1234",
            entryTime = Instant.parse("2025-06-22T22:22:33Z"),
            exitTime = null,
            latitude = null,
            longitude = null,
            eventType = EventType.ENTRY
        )

        whenever(webhookService.handleEvent(any())).thenAnswer { }

        // When/Then
        webTestClient.post()
            .uri("/webhook")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(webhookDto)
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .isEqualTo("Webhook processed successfully")
    }

    @Test
    fun `should return 400 Bad Request when there is an error processing the webhook`() = runTest {
        // Given
        val webhookDto = WebhookDto(
            licensePlate = "ABC1234",
            entryTime = Instant.parse("2025-06-22T22:22:33Z"),
            exitTime = null,
            latitude = null,
            longitude = null,
            eventType = EventType.ENTRY
        )

        whenever(webhookService.handleEvent(any())).doThrow(RuntimeException("Test exception"))

        // When/Then
        webTestClient.post()
            .uri("/webhook")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(webhookDto)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .isEqualTo("Error processing webhook request")
    }
}
