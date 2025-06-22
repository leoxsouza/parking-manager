package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotStatusRequestDto(
    @JsonProperty("lat")
    val latitude: Double,

    @JsonProperty("lng")
    val longitude: Double
)
