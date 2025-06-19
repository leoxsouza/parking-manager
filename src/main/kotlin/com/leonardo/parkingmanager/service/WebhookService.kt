package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import dev.krud.spring.componentmap.ComponentMap
import org.springframework.stereotype.Service

@Service
class WebhookService {

    @ComponentMap
    private lateinit var eventTypeStrategyMap: Map<EventType, WebhookEventStrategy>

    suspend fun handleEvent(event: WebhookDto) = eventTypeStrategyMap[event.eventType]?.handleEvent(event)

}