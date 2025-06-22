package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.model.ParkingSession
import com.leonardo.parkingmanager.model.Sector
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import com.leonardo.parkingmanager.repository.SectorRepository
import com.leonardo.parkingmanager.repository.SpotRepository
import com.leonardo.parkingmanager.service.PricingService
import com.leonardo.parkingmanager.service.WebhookEventStrategy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

/**
 * Strategy implementation for handling EXIT events.
 * This strategy updates an existing parking session with exit time and calculates the final price
 * based on the duration of the stay.
 */
@Component
class ExitEventStrategy(
    private val parkingSessionRepository: ParkingSessionRepository,
    private val spotRepository: SpotRepository,
    private val sectorRepository: SectorRepository,
    private val pricingService: PricingService
) : WebhookEventStrategy {
    override val eventType = EventType.EXIT
    private val logger = LoggerFactory.getLogger(ExitEventStrategy::class.java)

    /**
     * Handles the EXIT event by updating the parking session with exit time and calculating the final price.
     *
     * @param event The webhook event data
     * @throws IllegalArgumentException if required parameters are missing
     * @throws IllegalStateException if session cannot be found
     */
    override suspend fun handleEvent(event: WebhookDto) {
        logger.info("Handling EXIT event for license plate ${event.licensePlate}")
        
        validateEventData(event)
        
        val session = findActiveParkingSession(event.licensePlate)
        val spotId = session.spotId ?: throw IllegalStateException("Parking session has no associated spot")
        val sector = findSectorBySpotId(spotId)
        
        val parkedTime = session.parkedTime ?: throw IllegalStateException("Parking session has no parked time")
        val exitTime = event.exitTime ?: Instant.now()
        val durationMinutes = calculateDurationInMinutes(parkedTime, exitTime)
        
        val dynamicPrice = session.price ?: sector.basePrice
        
        val finalPrice = pricingService.calculateFinalPrice(dynamicPrice, durationMinutes, sector.durationLimitMinutes)
        
        updateParkingSession(session, exitTime, finalPrice)
    }
    
    /**
     * Validates that the event contains all required data
     */
    private fun validateEventData(event: WebhookDto) {
        if (event.licensePlate.isBlank()) {
            logger.error("Missing license plate for EXIT event")
            throw IllegalArgumentException("License plate is required for EXIT event")
        }
    }
    
    /**
     * Finds an active parking session by license plate
     */
    private suspend fun findActiveParkingSession(licensePlate: String): ParkingSession {
        return parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(licensePlate)
            .firstOrNull() ?: run {
                logger.error("No active parking session found for license plate $licensePlate")
                throw IllegalStateException("No active parking session found for license plate $licensePlate")
            }
    }
    
    /**
     * Finds the sector associated with a spot ID
     */
    private suspend fun findSectorBySpotId(spotId: Long): Sector {
        val spot = spotRepository.findById(spotId)
            ?: throw IllegalStateException("No spot found with ID $spotId")
        
        return sectorRepository.findByName(spot.sectorName) ?: run {
            logger.error("No sector found with name: ${spot.sectorName}")
            throw IllegalStateException("No sector found with name: ${spot.sectorName}")
        }
    }
    
    /**
     * Calculates the duration between entry time and exit time in minutes
     */
    private fun calculateDurationInMinutes(entryTime: Instant, exitTime: Instant): Long {
        return Duration.between(entryTime, exitTime).toMinutes()
    }

    /**
     * Updates and saves the parking session with exit time and final price
     */
    private suspend fun updateParkingSession(session: ParkingSession, exitTime: Instant, finalPrice: Double) {
        session.apply {
            this.exitTime = exitTime
            this.price = finalPrice
        }
        
        logger.info("Completing parking session: id=${session.id}, licensePlate=${session.licensePlate}, " +
                   "duration=${calculateDurationInMinutes(session.parkedTime!!, exitTime)} minutes, price=$finalPrice")
        parkingSessionRepository.save(session)
    }
}