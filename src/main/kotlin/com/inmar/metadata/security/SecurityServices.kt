package com.inmar.metadata.security

import com.inmar.metadata.repository.AppUserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Service
class CustomUserDetailsService(private val userRepo: AppUserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepo.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }
        return User(user.username, user.password, listOf(SimpleGrantedAuthority("ROLE_${user.role}")))
    }
}

@Service
class JwtService(
    @Value("\${app.security.jwt.secret}") private val secret: String,
    @Value("\${app.security.jwt.expiration-ms}") private val expirationMs: Long
) {
    private val log = LoggerFactory.getLogger(JwtService::class.java)

    private fun signingKey(): SecretKey =
        Keys.hmacShaKeyFor(secret.padEnd(32, '!').toByteArray(StandardCharsets.UTF_8))

    fun generateToken(username: String, role: String): String {
        log.debug("Generating JWT for user={}", username)
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(signingKey())
            .compact()
    }

    fun validateToken(token: String): Boolean = runCatching { parseClaims(token); true }.getOrDefault(false)

    fun getUsername(token: String): String = parseClaims(token).subject

    fun getRole(token: String): String = parseClaims(token)["role"] as String

    fun getExpirationMs() = expirationMs

    private fun parseClaims(token: String): Claims =
        Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).payload
}

class JwtAuthFilter(private val jwtService: JwtService, private val uds: UserDetailsService) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            if (jwtService.validateToken(token)) {
                val username = jwtService.getUsername(token)
                val userDetails = uds.loadUserByUsername(username)
                val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = auth
                log.debug("JWT auth set for user={}", username)
            }
        }
        chain.doFilter(req, res)
    }
}
