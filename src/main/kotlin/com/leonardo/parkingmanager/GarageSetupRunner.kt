package com.leonardo.parkingmanager

import com.leonardo.parkingmanager.service.GarageSetupService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class GarageSetupRunner(
    private val garageSetupService: GarageSetupService
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        runBlocking {
            garageSetupService.fetchAndPersistGarage()
        }
    }
}
