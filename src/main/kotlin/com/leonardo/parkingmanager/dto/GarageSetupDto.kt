package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GarageSetupDto(
    val garage: List<SectorDto>,
    val spots: List<SpotDto>
) {
    data class SectorDto(
        @JsonProperty("sector")
        val sector: String,
        @JsonProperty("basePrice")
        val basePrice: Double,
        @JsonProperty("max_capacity")
        val maxCapacity: Int,
        @JsonProperty("open_hour")
        val openHour: String?,
        @JsonProperty("close_hour")
        val closeHour: String?,
        @JsonProperty("duration_limit_minutes")
        val durationLimitMinutes: Int,
    )

    data class SpotDto(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("sector")
        val sector: String,
        @JsonProperty("lat")
        val lat: Double,
        @JsonProperty("lng")
        val lng: Double,
        @JsonProperty("occupied")
        val occupied: Boolean
    )
}
