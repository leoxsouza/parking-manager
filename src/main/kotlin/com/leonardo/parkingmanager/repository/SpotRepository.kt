package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.Spot
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SpotRepository : CoroutineCrudRepository<Spot, Long>
