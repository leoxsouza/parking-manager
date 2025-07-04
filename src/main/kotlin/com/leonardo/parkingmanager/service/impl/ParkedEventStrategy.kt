package com.leonardo.parkingmanager.service.impl

import com.leonardo.parkingmanager.dto.WebhookDto
import com.leonardo.parkingmanager.dto.enums.EventType
import com.leonardo.parkingmanager.exception.SpotAlreadyOccupiedException
import com.leonardo.parkingmanager.model.ParkingSession
import com.leonardo.parkingmanager.model.Sector
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import com.leonardo.parkingmanager.repository.SectorRepository
import com.leonardo.parkingmanager.service.PricingService
import com.leonardo.parkingmanager.service.SpotService
import com.leonardo.parkingmanager.service.WebhookEventStrategy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Strategy implementation for handling PARKED events.
 * This strategy updates an existing parking session with spot information and calculates the price
 * based on the current sector occupancy.
 */
@Component
class ParkedEventStrategy(
    private val parkingSessionRepository: ParkingSessionRepository,
    private val sectorRepository: SectorRepository,
    private val pricingService: PricingService,
    private val spotService: SpotService
) : WebhookEventStrategy {
    override val eventType = EventType.PARKED
    private val logger = LoggerFactory.getLogger(ParkedEventStrategy::class.java)

    /**
     * Handles the PARKED event by updating the parking session with spot information and price.
     *
     * @param event The webhook event data
     * @throws IllegalArgumentException if required parameters are missing
     * @throws IllegalStateException if spot or session cannot be found
     */
    override suspend fun handleEvent(event: WebhookDto) {
        logger.info("Handling PARKED event for license plate ${event.licensePlate}")
        validateEventData(event)

        val spot = spotService.findSpot(event.latitude!!, event.longitude!!)

        if (spot.occupied) {
            logger.error("Spot ${spot.id} is already occupied")
            throw SpotAlreadyOccupiedException("Spot ${spot.id} is already occupied")
        }

        val sector = findSector(spot.sectorName)
        val session = findParkingSession(event.licensePlate)
        
        val occupancy = calculateSectorOccupancy(sector, spot.sectorName)
        val price = pricingService.calculateDynamicPrice(sector.basePrice, occupancy)
        
        updateParkingSession(session, spot.id!!, price)

        spot.apply { this.occupied = true }
        spotService.updateSpot(spot)
    }
    
    /**
     * Validates that the event contains all required data
     */
    private fun validateEventData(event: WebhookDto) {
        if (event.latitude == null) {
            logger.error("Missing latitude for PARKED event: ${event.licensePlate}")
            throw IllegalArgumentException("Latitude is required for PARKED event")
        }
        if (event.longitude == null) {
            logger.error("Missing longitude for PARKED event: ${event.licensePlate}")
            throw IllegalArgumentException("Longitude is required for PARKED event")
        }
    }
    

    /**
     * Finds a sector by name
     */
    private suspend fun findSector(sectorName: String): Sector {
        return sectorRepository.findByName(sectorName) ?: run {
            logger.error("No sector found with name: $sectorName")
            throw IllegalStateException("No sector found with name: $sectorName")
        }
    }
    
    /**
     * Finds an active parking session by license plate
     */
    private suspend fun findParkingSession(licensePlate: String): ParkingSession {
        return parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(licensePlate).firstOrNull() ?: run {
            logger.error("No active parking session found for license plate $licensePlate")
            throw IllegalStateException("No active parking session found for license plate $licensePlate")
        }
    }
    
    /**
     * Calculates the current occupancy rate of a sector
     */
    private suspend fun calculateSectorOccupancy(sector: Sector, sectorName: String): Double {
        val activeSessionsCount = parkingSessionRepository.countActiveSessionsBySector(sectorName)
        logger.debug("Sector $sectorName occupancy: $activeSessionsCount/${sector.maxCapacity}")

        if (activeSessionsCount >= sector.maxCapacity || sector.maxCapacity <= 0) {
            logger.error("Sector $sectorName is full")
            throw IllegalStateException("Sector $sectorName is full")
        }

        return activeSessionsCount.toDouble() / sector.maxCapacity
    }
    
    /**
     * Updates and saves the parking session with new information
     */
    private suspend fun updateParkingSession(session: ParkingSession, spotId: Long, price: Double) {
        session.apply {
            this.parkedTime = Instant.now()
            this.price = price
            this.spotId = spotId
        }
        
        logger.info("Updating parking session: id=${session.id}, licensePlate=${session.licensePlate}, price=$price")
        parkingSessionRepository.save(session)
    }
}