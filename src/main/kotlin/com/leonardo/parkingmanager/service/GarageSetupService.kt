package com.leonardo.parkingmanager.service

import com.leonardo.parkingmanager.dto.GarageSetupDto
import com.leonardo.parkingmanager.model.Garage
import com.leonardo.parkingmanager.model.Sector
import com.leonardo.parkingmanager.repository.GarageRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.time.LocalTime

@Service
class GarageSetupService(
    private val garageRepository: GarageRepository,
    @Value("\${garage.api.url:http://localhost:3000/garage}")
    private val garageApiUrl: String
) {
    private val restTemplate = RestTemplate()

    @Transactional
    fun fetchAndPersistGarage() {
        val dto = restTemplate.getForObject(garageApiUrl, GarageSetupDto::class.java) ?: return
        val garage = Garage(
            name = "Main Garage",
            sectors = mutableListOf()
        )
        val savedGarage = garageRepository.save(garage)

        val sectors = dto.garage.map { sectorDto ->
            Sector(
                name = sectorDto.sector,
                basePrice = sectorDto.basePrice,
                maxCapacity = sectorDto.maxCapacity,
                openHour = sectorDto.openHour?.let { LocalTime.parse(it) } ?: LocalTime.of(0, 0),
                closeHour = sectorDto.closeHour?.let { LocalTime.parse(it) } ?: LocalTime.of(0, 0),
                durationLimitMinutes = sectorDto.durationLimitMinutes,
                garage = savedGarage
            )
        }

        val garageWithSectors = savedGarage.apply {
            this.sectors.addAll(sectors)
        }
        garageRepository.save(garageWithSectors)
    }
}
