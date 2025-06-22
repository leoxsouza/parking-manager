package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LicensePlateDto(
    @JsonProperty("license_plate")
    val licensePlate: String
)
