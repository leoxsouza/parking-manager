package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.Sector
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SectorRepository : CoroutineCrudRepository<Sector, String> {
    suspend fun findByName(name: String): Sector?
}
