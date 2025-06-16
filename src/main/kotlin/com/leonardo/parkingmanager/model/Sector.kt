package com.leonardo.parkingmanager.model

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalTime

@Entity
data class Sector (
    @Id
    val name: String = "",

    val basePrice: Double = 0.0,

    val maxCapacity: Int = 0,

    val openHour: LocalTime = LocalTime.of(0, 0),

    val closeHour: LocalTime = LocalTime.of(0, 0),

    val durationLimitMinutes: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garage_id")
    val garage: Garage? = null
)