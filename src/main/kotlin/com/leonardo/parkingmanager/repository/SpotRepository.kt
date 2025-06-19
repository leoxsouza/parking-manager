package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.Spot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpotRepository : JpaRepository<Spot, Long>
