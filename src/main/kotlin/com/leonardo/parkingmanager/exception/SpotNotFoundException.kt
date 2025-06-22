package com.leonardo.parkingmanager.exception

class SpotNotFoundException(lat: Double, lng: Double): ResourceNotFoundException("Spot not found for coordinates: ($lat, $lng)")