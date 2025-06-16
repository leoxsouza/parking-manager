package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.Garage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GarageRepository : JpaRepository<Garage, Long>
