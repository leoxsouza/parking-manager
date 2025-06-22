package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class RevenueRequestDto(
    @JsonProperty("date")
    val date: LocalDate,

    @JsonProperty("sector")
    val sector: String
)
