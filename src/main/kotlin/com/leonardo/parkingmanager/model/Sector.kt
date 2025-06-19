package com.leonardo.parkingmanager.model

import jakarta.persistence.*
import java.time.LocalTime

@Entity
data class Sector (
    @Id
    val name: String = "",

    val basePrice: Double = 0.0,

    val maxCapacity: Int = 0,

    val openHour: LocalTime = LocalTime.of(0, 0),

    val closeHour: LocalTime? = null,

    val durationLimitMinutes: Int = 0,
)