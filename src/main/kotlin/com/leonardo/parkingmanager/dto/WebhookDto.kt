package com.leonardo.parkingmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.leonardo.parkingmanager.dto.enums.EventType

data class WebhookDto(
    @JsonProperty("license_plate")
    val licensePlate: String,

    @JsonProperty("entry_time")
    val entryTime: String,

    @JsonProperty("exit_time")
    val exitTime: String,

    @JsonProperty("lat")
    val latitude: Double,

    @JsonProperty("lng")
    val longitude: Double,

    @JsonProperty("event_type")
    val eventType: EventType,
)
