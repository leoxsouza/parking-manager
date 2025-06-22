package com.leonardo.parkingmanager.dto.mapper

import com.leonardo.parkingmanager.dto.GarageSetupDto
import com.leonardo.parkingmanager.model.Sector
import com.leonardo.parkingmanager.model.Spot
import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class GarageMapper {

    fun toSector(dto: GarageSetupDto.SectorDto): Sector {
        return Sector(
            name = dto.sector,
            basePrice = dto.basePrice,
            maxCapacity = dto.maxCapacity,
            openHour = dto.openHour?.let { LocalTime.parse(it) } ?: LocalTime.of(0, 0),
            closeHour = dto.closeHour?.let { LocalTime.parse(it) } ?: LocalTime.of(0, 0),
            durationLimitMinutes = dto.durationLimitMinutes,
            id = null
        )
    }

    fun toSpot(dto: GarageSetupDto.SpotDto, sector: Sector): Spot {
        return Spot(
            id = null,
            lat = dto.lat,
            lng = dto.lng,
            sectorName = sector.name,
            occupied = dto.occupied
        )
    }

}