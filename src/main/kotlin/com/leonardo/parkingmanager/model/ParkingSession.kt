package com.leonardo.parkingmanager.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
data class ParkingSession(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val licensePlate: String = "",

    val entryTime: OffsetDateTime = OffsetDateTime.now(),

    var parkedTime: OffsetDateTime? = null,

    var exitTime: OffsetDateTime? = null,

    var spotLat: Double? = null,

    var spotLng: Double? = null,

    var price: Double? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_name")
    val sector: Sector = Sector(),
)
