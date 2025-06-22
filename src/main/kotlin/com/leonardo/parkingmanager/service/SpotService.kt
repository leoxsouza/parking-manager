package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.SpotStatusRequestDto
import com.leonardo.parkingmanager.dto.SpotStatusResponseDto
import com.leonardo.parkingmanager.exception.SpotNotFoundException
import com.leonardo.parkingmanager.model.Spot
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import com.leonardo.parkingmanager.repository.SpotRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SpotService(
    private val spotRepository: SpotRepository,
    private val parkingSessionRepository: ParkingSessionRepository
) {

    private val logger = LoggerFactory.getLogger(SpotService::class.java)

    suspend fun getSpotStatus(request: SpotStatusRequestDto): SpotStatusResponseDto {
        val spot = findSpot(request.latitude, request.longitude)

        val session = parkingSessionRepository.findActiveByLatAndLng(request.latitude, request.longitude)

        return if (session != null) {
            SpotStatusResponseDto(
                spot.occupied,
                session.entryTime,
                session.parkedTime
            )
        } else {
            SpotStatusResponseDto(
                spot.occupied,
                null,
                null
            )
        }

    }


    /**
     * Finds a parking spot at the specified geographical coordinates.
     *
     * This method queries the spot repository to locate a parking spot at the exact
     * latitude and longitude provided. If no spot exists at the specified coordinates,
     * a [SpotNotFoundException] is thrown.
     *
     * @param latitude The latitude coordinate of the spot to find
     * @param longitude The longitude coordinate of the spot to find
     * @return The [Spot] object found at the specified coordinates
     * @throws SpotNotFoundException If no spot exists at the specified coordinates
     */
    suspend fun findSpot(latitude: Double, longitude: Double): Spot {
        return spotRepository.findSpotByLatAndLng(latitude, longitude)
            .firstOrNull() ?: run {
            logger.error("No spot found near coordinates: $latitude, $longitude")
            throw SpotNotFoundException(latitude, longitude)
        }
    }

    suspend fun updateSpot(spot: Spot) {
        spotRepository.save(spot)
    }

}