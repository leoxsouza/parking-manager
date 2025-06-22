package com.leonardo.parkingmanager.service

import org.springframework.stereotype.Service

@Service
class PricingService {
    /**
     * Calculates the dynamic price based on sector occupancy
     * 
     * @param basePrice The base price for the sector
     * @param occupancy The current occupancy rate of the sector (0.0 to 1.0)
     * @return The calculated price adjusted for occupancy
     */
    fun calculateDynamicPrice(basePrice: Double, occupancy: Double): Double {
        return when {
            occupancy < 0.25 -> basePrice * 0.9
            occupancy < 0.5  -> basePrice
            occupancy < 0.75 -> basePrice * 1.10
            else             -> basePrice * 1.25
        }
    }
}