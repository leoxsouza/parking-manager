package com.leonardo.parkingmanager.controller

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.service.WebhookService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
class WebhookController(
    private val webhookService: WebhookService
) {
    private val logger = LoggerFactory.getLogger(WebhookController::class.java)

    @PostMapping
    suspend fun handleEvent(@RequestBody webhookDto: WebhookDto): ResponseEntity<String> {
        logger.info("Handling webhook event: $webhookDto")
        webhookService.handleEvent(webhookDto)
        return ResponseEntity.ok("Webhook processed successfully")
    }
}