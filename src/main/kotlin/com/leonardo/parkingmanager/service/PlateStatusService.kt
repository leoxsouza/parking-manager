package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.LicensePlateDto
import com.leonardo.parkingmanager.dto.PlateStatusResponseDto
import com.leonardo.parkingmanager.exception.ResourceNotFoundException
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import org.springframework.stereotype.Service

@Service
class PlateStatusService(
    private val parkingSessionRepository: ParkingSessionRepository
) {
    val logger = org.slf4j.LoggerFactory.getLogger(PlateStatusService::class.java)

    suspend fun getPlateStatus(licensePlate: LicensePlateDto): PlateStatusResponseDto {
        return parkingSessionRepository.findStatusByLicensePlate(licensePlate.licensePlate) ?: run {
            logger.error("No active parking session found for license plate ${licensePlate.licensePlate}")
            throw ResourceNotFoundException("No active parking session found for license plate ${licensePlate.licensePlate}")
        }
    }
}