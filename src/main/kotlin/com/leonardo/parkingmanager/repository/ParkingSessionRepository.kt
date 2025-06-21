package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.ParkingSession
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ParkingSessionRepository : CoroutineCrudRepository<ParkingSession, Long>
