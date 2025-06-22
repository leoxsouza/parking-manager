package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.model.ParkingSession
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import com.leonardo.parkingmanager.service.WebhookEventStrategy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EntryEventStrategy(
    private val parkingSessionRepository: ParkingSessionRepository
) : WebhookEventStrategy {
    override val eventType = EventType.ENTRY

    private val logger = LoggerFactory.getLogger(EntryEventStrategy::class.java)

    override suspend fun handleEvent(event: WebhookDto) {
        logger.info("Handling ENTRY event for license plate ${event.licensePlate}")
        val session = ParkingSession(
            licensePlate = event.licensePlate,
            entryTime = event.entryTime
        )

        // TODO: Implement validations

        parkingSessionRepository.save(session)
    }
}