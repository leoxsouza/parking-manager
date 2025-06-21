package com.leonardo.parkingmanager.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalTime

@Table("sector")
data class Sector (
    @Id
    val id: Long?,

    @Column("name")
    val name: String,

    @Column("base_price")
    val basePrice: Double,

    @Column("max_capacity")
    val maxCapacity: Int,

    @Column("open_hour")
    val openHour: LocalTime,

    @Column("close_hour")
    val closeHour: LocalTime,

    @Column("duration_limit_minutes")
    val durationLimitMinutes: Int,
)