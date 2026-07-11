package com.pucetec.roles.controllers

import com.pucetec.roles.dto.CreateParkingSpaceRequest
import com.pucetec.roles.dto.ParkingSpaceResponse
import com.pucetec.roles.ParkingSpaceService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/parking-spaces")
class ParkingSpaceController(
    private val parkingSpaceService: ParkingSpaceService
) {

    private val logger = LoggerFactory.getLogger(ParkingSpaceController::class.java)

    // PÚBLICO: consultar espacios disponibles (regla permitAll en SecurityConfig).
    @GetMapping("/available")
    fun getAvailable(): List<ParkingSpaceResponse> {
        logger.info("Listing available parking spaces")
        return parkingSpaceService.getAvailableSpaces()
    }

    // PRIVADO: requiere token válido. (En el DEBER se restringe SOLO a ADMIN.)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun createSpace(@RequestBody request: CreateParkingSpaceRequest): ParkingSpaceResponse {
        logger.info("Creating parking space ${request.code}")
        return parkingSpaceService.createSpace(request)
    }
}