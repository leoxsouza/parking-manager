package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class PlateStatusResponseDto(
    @JsonProperty("license_plate")
    val licensePlate: String,

    @JsonProperty("price_until_now")
    val priceUntilNow: Double,

    @JsonProperty("entry_time")
    val entryTime: Instant,

    @JsonProperty("time_parked")
    val timeParked: Instant,

    @JsonProperty("lat")
    val latitude: Double,

    @JsonProperty("lng")
    val longitude: Double
)
