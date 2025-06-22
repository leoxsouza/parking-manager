package com.leonardo.parkingmanager.controller

import com.leonardo.parkingmanager.dto.LicensePlateDto
import com.leonardo.parkingmanager.dto.PlateStatusResponseDto
import com.leonardo.parkingmanager.service.PlateStatusService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/plate-status")
class PlateStatusController(
    private val plateStatusService: PlateStatusService
) {

    @PostMapping
    suspend fun getPlateStatus(@RequestBody licensePlate: LicensePlateDto): ResponseEntity<PlateStatusResponseDto> {
        return ResponseEntity.ok(plateStatusService.getPlateStatus(licensePlate))
    }
}