package com.leonardo.parkingmanager.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

@Entity
data class Revenue(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val sector: String = "",

    val date: LocalDate = LocalDate.now(),

    val amount: Double = 0.0,

    val timestamp: OffsetDateTime = OffsetDateTime.now()
)
