package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.service.WebhookEventStrategy
import org.springframework.stereotype.Component

@Component
class ParkedEventStrategy: WebhookEventStrategy {
    override val eventType = EventType.PARKED

    override suspend fun handleEvent(event: WebhookDto) {
        println(event)
        TODO("Not yet implemented")
    }
}