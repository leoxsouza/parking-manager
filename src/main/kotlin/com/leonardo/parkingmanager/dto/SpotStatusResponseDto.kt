package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class SpotStatusResponseDto(
    @JsonProperty("occupied")
    val occupied: Boolean,

    @JsonProperty("entry_time")
    val entryTime: Instant?,

    @JsonProperty("time_parked")
    val timeParked: Instant?,
)
