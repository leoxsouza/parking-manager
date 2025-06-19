package com.leonardo.parkingmanager.controller

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.service.WebhookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
class WebhookController(private val webhookService: WebhookService) {

    @PostMapping
    suspend fun handleEvent(@RequestBody event: WebhookDto): ResponseEntity<Unit> {
        webhookService.handleEvent(event)
        return ResponseEntity.ok().build()
    }
}