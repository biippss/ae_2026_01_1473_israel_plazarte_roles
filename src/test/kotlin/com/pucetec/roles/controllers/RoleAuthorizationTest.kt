package com.pucetec.roles.controllers

import com.pucetec.roles.entities.ParkingSpace
import com.pucetec.roles.repositories.ParkingSpaceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class RoleAuthorizationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var parkingSpaceRepository: ParkingSpaceRepository

    private var freeSpaceId: Long = 0

    @BeforeEach
    fun setup() {
        val uniqueCode = "FREE_${System.currentTimeMillis()}"
        val space = parkingSpaceRepository.save(ParkingSpace(code = uniqueCode, occupied = false))
        freeSpaceId = space.id
    }

    @Test
    fun `un USER no puede crear espacios y recibe 403`() {
        mockMvc.perform(
            post("/parking-spaces")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"code":"TEST_${System.currentTimeMillis()}"}""")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `un ADMIN si puede crear espacios`() {
        mockMvc.perform(
            post("/parking-spaces")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"code":"TEST_${System.currentTimeMillis()}"}""")
        ).andExpect(status().isCreated)
    }

    @Test
    fun `sin token no autenticado recibe 401`() {
        mockMvc.perform(
            post("/parking-spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"code":"TEST_${System.currentTimeMillis()}"}""")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `un ADMIN no puede registrar entrada y recibe 403`() {
        mockMvc.perform(
            post("/tickets/entry")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"plate":"TEST-999","parkingSpaceId":$freeSpaceId}""")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `un USER si puede registrar entrada`() {
        mockMvc.perform(
            post("/tickets/entry")
                .with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"plate":"TEST-888","parkingSpaceId":$freeSpaceId}""")
        ).andExpect(status().isCreated)
    }
}