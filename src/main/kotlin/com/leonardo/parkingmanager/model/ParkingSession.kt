package com.leonardo.parkingmanager.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("parking_session")
data class ParkingSession(
    @Id
    val id: Long = 0,

    val licensePlate: String = "",

    val entryTime: Instant? = null,

    var parkedTime: Instant? = null,

    var exitTime: Instant? = null,

    var spotLat: Double? = null,

    var spotLng: Double? = null,

    var price: Double? = null,
)
