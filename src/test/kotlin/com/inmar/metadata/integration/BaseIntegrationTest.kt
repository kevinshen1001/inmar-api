package com.inmar.metadata.integration

import com.inmar.metadata.repository.AppUserRepository
import com.inmar.metadata.entity.AppUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseIntegrationTest {

    @Autowired
    private lateinit var userRepo: AppUserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeAll
    fun ensureTestUsers() {
        // Always ensure users exist with a programmatically encoded password
        // This bypasses any BCrypt hash issues in SQL seed data
        val encoded = passwordEncoder.encode("password")
        listOf("admin" to "ADMIN", "user" to "USER").forEach { (username, role) ->
            if (!userRepo.findByUsername(username).isPresent) {
                userRepo.save(AppUser(username = username, password = encoded, role = role))
            } else {
                val existing = userRepo.findByUsername(username).get()
                userRepo.save(existing.copy(password = encoded))
            }
        }
    }

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("inmar_test")
            withUsername("test")
            withPassword("test")
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
