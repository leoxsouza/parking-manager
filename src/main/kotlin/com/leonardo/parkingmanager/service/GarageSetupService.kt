package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.GarageSetupDto
import com.leonardo.parkingmanager.dto.mapper.GarageMapper
import com.leonardo.parkingmanager.repository.SectorRepository
import com.leonardo.parkingmanager.repository.SpotRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class GarageSetupService(
    @Value("\${garage.api.url:http://localhost:3000/garage}")
    private val garageApiUrl: String,
    private val mapper: GarageMapper,
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository
) {
    private val restTemplate = RestTemplate()

    @Transactional
    fun fetchAndPersistGarage() {
        val dto = restTemplate.getForObject(garageApiUrl, GarageSetupDto::class.java) ?: return

        val sectors = dto.garage.map { mapper.toSector(it) }
        sectorRepository.saveAll(sectors)

        val sectorMap = sectors.associateBy { it.name }
        val spots = dto.spots.map {
            val sector = sectorMap[it.sector] ?: error("Sector not found: ${it.sector}")
            mapper.toSpot(it, sector)
        }
        spotRepository.saveAll(spots)
    }
}
