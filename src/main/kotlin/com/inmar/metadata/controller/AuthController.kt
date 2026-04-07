package com.inmar.metadata.controller

import com.inmar.metadata.dto.ApiResponse
import com.inmar.metadata.dto.LoginRequest
import com.inmar.metadata.dto.LoginResponse
import com.inmar.metadata.repository.AppUserRepository
import com.inmar.metadata.security.JwtService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userRepo: AppUserRepository
) {
    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        log.info("Login attempt for user={}", req.username)
        authManager.authenticate(UsernamePasswordAuthenticationToken(req.username, req.password))
        val user = userRepo.findByUsername(req.username).orElseThrow()
        val token = jwtService.generateToken(user.username, user.role)
        log.info("Login successful for user={}", req.username)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Login successful",
            data = LoginResponse(token, user.username, user.role, jwtService.getExpirationMs())
        ))
    }
}
