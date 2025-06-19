package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.service.WebhookEventStrategy
import org.springframework.stereotype.Component

@Component
class ExitEventStrategy: WebhookEventStrategy {
    override val eventType = EventType.EXIT

    override suspend fun handleEvent(event: WebhookDto) {
        println(event)
        TODO("Not yet implemented")
    }
}