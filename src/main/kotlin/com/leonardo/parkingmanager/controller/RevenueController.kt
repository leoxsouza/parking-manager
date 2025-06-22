package com.leonardo.parkingmanager.controller

import com.leonardo.parkingmanager.dto.RevenueRequestDto
import com.leonardo.parkingmanager.dto.RevenueResponseDto
import com.leonardo.parkingmanager.service.RevenueService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/revenue")
class RevenueController(
    private val revenueService: RevenueService
) {

    @PostMapping
    suspend fun calculateRevenue(@RequestBody revenueRequestDto: RevenueRequestDto): ResponseEntity<RevenueResponseDto> {
        return ResponseEntity.ok(revenueService.calculateRevenue(revenueRequestDto))
    }
}