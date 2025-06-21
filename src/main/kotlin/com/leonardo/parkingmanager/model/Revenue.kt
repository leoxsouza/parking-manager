package com.leonardo.parkingmanager.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.OffsetDateTime

@Table("revenue")
data class Revenue(
    @Id
    val id: Long = 0,

    val sector: String = "",

    val date: LocalDate = LocalDate.now(),

    val amount: Double = 0.0,

    val timestamp: OffsetDateTime = OffsetDateTime.now()
)
