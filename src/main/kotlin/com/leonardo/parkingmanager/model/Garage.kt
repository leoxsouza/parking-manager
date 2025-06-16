package com.leonardo.parkingmanager.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
data class Garage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    @OneToMany(mappedBy = "garage", cascade = [CascadeType.ALL], orphanRemoval = true)
    val sectors: MutableList<Sector> = mutableListOf()
)
