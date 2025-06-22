package com.leonardo.parkingmanager.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("spot")
data class Spot(
    @Id
    val id: Long?,
    val lat: Double,
    val lng: Double,
    @Column("sector_name")
    val sectorName: String,
    var occupied: Boolean
)
