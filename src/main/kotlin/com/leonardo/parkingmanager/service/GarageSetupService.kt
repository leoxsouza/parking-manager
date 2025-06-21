package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.GarageSetupDto
import com.leonardo.parkingmanager.dto.mapper.GarageMapper
import com.leonardo.parkingmanager.repository.SectorRepository
import com.leonardo.parkingmanager.repository.SpotRepository
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class GarageSetupService(
    @Value("\${garage.api.url:http://localhost:3000/garage}")
    private val garageApiUrl: String,
    private val mapper: GarageMapper,
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository,
) {
    private val webClient = WebClient.builder().build()
    private val logger = LoggerFactory.getLogger(GarageSetupService::class.java)

    suspend fun fetchAndPersistGarage() {
        val dto = try {
            webClient.get()
                .uri(garageApiUrl)
                .retrieve()
                .awaitBody<GarageSetupDto>()
        } catch (e: Exception) {
            logger.error("Error fetching garage setup from API: ${e.message}", e)
            return
        }

        val sectors = dto.garage.map { mapper.toSector(it) }
        val savedSectors = sectorRepository.saveAll(sectors).toList()

        val sectorMap = savedSectors.associateBy { it.name }
        val spots = dto.spots.map {
            val sector = sectorMap[it.sector] ?: error("Sector not found: ${it.sector}")
            mapper.toSpot(it, sector)
        }

        spotRepository.saveAll(spots).toList()
    }
}
