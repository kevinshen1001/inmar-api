package com.inmar.metadata.integration

import com.inmar.metadata.dto.LocationRequest
import com.inmar.metadata.repository.LocationRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class LocationControllerIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var locationRepo: LocationRepository

    private lateinit var adminToken: String

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        adminToken = obtainToken("admin", "password")
    }

    private fun obtainToken(username: String, password: String): String {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"$username","password":"$password"}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("data.token")
    }

    @Test
    @Order(1)
    fun `GET all locations returns seeded data`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", equalTo(true))
            .body("data", not(empty<Any>()))
            .body("data.name", hasItems("Perimeter", "Center"))
    }

    @Test
    @Order(2)
    fun `GET location by id returns correct location`() {
        val locationId = locationRepo.findByName("Perimeter").get().id

        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.name", equalTo("Perimeter"))
    }

    @Test
    @Order(3)
    fun `GET location by invalid id returns 404`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/99999")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("error", equalTo("Not Found"))
    }

    @Test
    @Order(4)
    fun `POST create location returns 201`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"TestLocation","description":"Test"}""")
            .post("/api/v1/location")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("data.name", equalTo("TestLocation"))
            .body("data.description", equalTo("Test"))
    }

    @Test
    @Order(5)
    fun `POST create duplicate location returns 409`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"Perimeter"}""")
            .post("/api/v1/location")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
    }

    @Test
    @Order(6)
    fun `POST create location with blank name returns 400`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":""}""")
            .post("/api/v1/location")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    @Order(7)
    fun `PUT update location succeeds`() {
        val locationId = locationRepo.findByName("TestLocation").get().id

        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"TestLocationUpdated","description":"Updated"}""")
            .put("/api/v1/location/$locationId")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.name", equalTo("TestLocationUpdated"))
    }

    @Test
    @Order(8)
    fun `DELETE location succeeds`() {
        val locationId = locationRepo.findByName("TestLocationUpdated").get().id

        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .delete("/api/v1/location/$locationId")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", equalTo(true))
    }

    @Test
    @Order(9)
    fun `GET locations requires authentication`() {
        RestAssured.given()
            .get("/api/v1/location")
            .then()
            .statusCode(anyOf(equalTo(401), equalTo(403)))
    }

    @Test
    @Order(10)
    fun `GET with Basic Auth works`() {
        RestAssured.given()
            .auth().basic("user", "password")
            .get("/api/v1/location")
            .then()
            .statusCode(HttpStatus.OK.value())
    }

    @Test
    @Order(11)
    fun `response contains X-Trace-Id header`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location")
            .then()
            .statusCode(HttpStatus.OK.value())
            .header("X-Trace-Id", notNullValue())
    }
}
