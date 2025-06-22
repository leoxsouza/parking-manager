package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.ParkingSession
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

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

    suspend fun findByLicensePlate(licensePlate: String): MutableList<ParkingSession>
}
