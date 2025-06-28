package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.Spot
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SpotRepository : CoroutineCrudRepository<Spot, Long> {
    suspend fun findSpotByLatAndLng(lat: Double, lng: Double): MutableList<Spot>

    @Query("""
        UPDATE spot
        SET occupied = false
        WHERE id = :spotId
    """)
    suspend fun freeUpSpot(spotId: Long)
}
