package com.leonardo.parkingmanager.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("parking_session")
data class ParkingSession(
    @Id
    val id: Long? = null,
    val licensePlate: String,
    val entryTime: Instant?,
    var parkedTime: Instant? = null,
    var exitTime: Instant? = null,
    var price: Double? = null,
    var spotId: Long? = null
)
