package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.RevenueRequestDto
import com.leonardo.parkingmanager.dto.RevenueResponseDto
import com.leonardo.parkingmanager.repository.ParkingSessionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@Service
class RevenueService(
    private val parkingSessionRepository: ParkingSessionRepository
) {

    private val logger = LoggerFactory.getLogger(RevenueService::class.java)

    /**
     * Calculates the total revenue for a given sector and date range.
     *
     * @param revenueRequestDto The request with the sector and date
     * @return The response with the calculated total revenue
     */
    suspend fun calculateRevenue(revenueRequestDto: RevenueRequestDto): RevenueResponseDto {
        logger.info("Calculating revenue for sector=${revenueRequestDto.sector} and date=${revenueRequestDto.date}")

        val startOfDay = revenueRequestDto.date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = revenueRequestDto.date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()

        logger.debug("Calculating revenue for sector=${revenueRequestDto.sector} and date=${revenueRequestDto.date} between $startOfDay and $endOfDay")

        
        val totalRevenue = parkingSessionRepository.calculateTotalRevenueBySectorAndDateRange(
            startDate = startOfDay,
            endDate = endOfDay,
            sector = revenueRequestDto.sector
        )

        logger.info("Calculated revenue for sector=${revenueRequestDto.sector} and date=${revenueRequestDto.date}: $totalRevenue")

        
        return RevenueResponseDto(
            amount = totalRevenue,
            currency = "BRL",
            timestamp = Instant.now()
        )
    }
}