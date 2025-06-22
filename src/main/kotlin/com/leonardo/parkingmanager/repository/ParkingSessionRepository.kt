package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.dto.PlateStatusResponseDto
import com.leonardo.parkingmanager.model.ParkingSession
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ParkingSessionRepository : CoroutineCrudRepository<ParkingSession, Long> {

    @Query("""
        SELECT COUNT(ps.id)
        FROM parking_session ps
        JOIN spot s ON ps.spot_id = s.id
        WHERE s.sector_name = :sectorName
        AND ps.exit_time IS NULL
    """)
    suspend fun countActiveSessionsBySector(sectorName: String): Long

    suspend fun findByLicensePlateAndExitTimeIsNull(licensePlate: String): MutableList<ParkingSession>

    @Query("""
        SELECT 
          ps.license_plate,
          ps.price AS price_until_now,
          ps.entry_time,
          ps.parked_time as time_parked,
          s.lat AS latitude,
          s.lng AS longitude
        FROM parking_session ps
        JOIN spot s ON ps.spot_id = s.id
        WHERE ps.license_plate = :licensePlate
          AND ps.exit_time IS NULL
        LIMIT 1
    """)
    suspend fun findStatusByLicensePlate(
        licensePlate: String
    ): PlateStatusResponseDto?


    @Query("""
        SELECT * FROM parking_session ps
        JOIN spot s ON ps.spot_id = s.id
        WHERE s.lat = :lat AND s.lng = :lng
          AND ps.exit_time IS NULL
        LIMIT 1
    """)
    suspend fun findActiveByLatAndLng(lat: Double, lng: Double): ParkingSession?
    
    @Query("""
        SELECT COALESCE(SUM(ps.price), 0.0) as total_revenue
        FROM parking_session ps
        JOIN spot s ON ps.spot_id = s.id
        WHERE ps.exit_time >= :startDate
        AND ps.exit_time <= :endDate
        AND s.sector_name = :sector
        AND ps.price IS NOT NULL
    """)
    suspend fun calculateTotalRevenueBySectorAndDateRange(
        startDate: Instant,
        endDate: Instant,
        sector: String
    ): Double
}
