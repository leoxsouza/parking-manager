package com.leonardo.parkingmanager.repository

import com.leonardo.parkingmanager.model.Sector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectorRepository : JpaRepository<Sector, String>
