package com.leonardo.parkingmanager.controller

import com.leonardo.parkingmanager.dto.SpotStatusRequestDto
import com.leonardo.parkingmanager.dto.SpotStatusResponseDto
import com.leonardo.parkingmanager.service.SpotService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class SpotController(
    val spotService: SpotService
) {

    @PostMapping("/spot-status")
    suspend fun getSpotStatus(@RequestBody requestBody: SpotStatusRequestDto): ResponseEntity<SpotStatusResponseDto> {
        return ResponseEntity.ok(spotService.getSpotStatus(requestBody))
    }
}