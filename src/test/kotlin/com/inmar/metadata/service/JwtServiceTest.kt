package com.inmar.metadata.service

import com.inmar.metadata.security.JwtService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private lateinit var jwtService: JwtService

    @BeforeEach
    fun setup() {
        jwtService = JwtService(
            secret = "test-secret-key-for-unit-testing-only",
            expirationMs = 3600000L
        )
    }

    @Test
    fun `generateToken returns non-blank token`() {
        val token = jwtService.generateToken("admin", "ADMIN")
        assertTrue(token.isNotBlank())
        assertTrue(token.contains("."))
    }

    @Test
    fun `validateToken returns true for valid token`() {
        val token = jwtService.generateToken("user", "USER")
        assertTrue(jwtService.validateToken(token))
    }

    @Test
    fun `validateToken returns false for tampered token`() {
        val token = jwtService.generateToken("user", "USER")
        val tampered = token.dropLast(5) + "XXXXX"
        assertFalse(jwtService.validateToken(tampered))
    }

    @Test
    fun `getUsername extracts correct username`() {
        val token = jwtService.generateToken("testuser", "USER")
        assertEquals("testuser", jwtService.getUsername(token))
    }

    @Test
    fun `getRole extracts correct role`() {
        val token = jwtService.generateToken("admin", "ADMIN")
        assertEquals("ADMIN", jwtService.getRole(token))
    }

    @Test
    fun `expired token fails validation`() {
        val expiredJwt = JwtService("test-secret-key-for-unit-testing-only", -1000L)
        val token = expiredJwt.generateToken("user", "USER")
        assertFalse(expiredJwt.validateToken(token))
    }

    @Test
    fun `validateToken returns false for completely invalid string`() {
        assertFalse(jwtService.validateToken("not.a.valid.jwt.token"))
    }

    @Test
    fun `getExpirationMs returns configured value`() {
        assertEquals(3600000L, jwtService.getExpirationMs())
    }
}
