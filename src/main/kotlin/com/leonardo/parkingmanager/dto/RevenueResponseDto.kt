package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class RevenueResponseDto(
    @JsonProperty("amount")
    val amount: Double,

    @JsonProperty("currency")
    val currency: String,

    @JsonProperty("timestamp")
    val timestamp: Instant
)
