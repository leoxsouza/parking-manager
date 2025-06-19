package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import dev.krud.spring.componentmap.ComponentMapKey

interface WebhookEventStrategy {
    @get:ComponentMapKey
    val eventType: EventType

    suspend fun handleEvent(event: WebhookDto)
}