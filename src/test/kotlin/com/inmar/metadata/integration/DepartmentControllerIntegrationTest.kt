package com.inmar.metadata.integration

import com.inmar.metadata.repository.LocationRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DepartmentControllerIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var locationRepo: LocationRepository

    private lateinit var adminToken: String
    private var perimeterId: Long = 0

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        adminToken = obtainToken("admin", "password")
        perimeterId = locationRepo.findByName("Perimeter").get().id
    }

    private fun obtainToken(username: String, password: String): String {
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"$username","password":"$password"}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
        return response.path("data.token")
            ?: error("Login failed for $username — token was null. Response: ${response.response().asString()}")
    }

    @Test
    @Order(1)
    fun `GET departments by location returns data`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$perimeterId/department")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", not(empty<Any>()))
            .body("data.name", hasItem("Bakery"))
    }

    @Test
    @Order(2)
    fun `GET departments for non-existent location returns 404`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/99999/department")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    @Order(3)
    fun `POST create department succeeds`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"TestDept","description":"A test dept"}""")
            .post("/api/v1/location/$perimeterId/department")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("data.name", equalTo("TestDept"))
            .body("data.locationName", equalTo("Perimeter"))
    }

    @Test
    @Order(4)
    fun `POST create duplicate department returns 409`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"Bakery"}""")
            .post("/api/v1/location/$perimeterId/department")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
    }

    @Test
    @Order(5)
    fun `user role cannot create departments`() {
        // Use Basic Auth directly — avoids a separate login call that could mask credential issues
        RestAssured.given()
            .auth().basic("user", "password")
            .contentType(ContentType.JSON)
            .body("""{"name":"AnotherDept"}""")
            .post("/api/v1/location/$perimeterId/department")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
    }
}
