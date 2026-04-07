package com.inmar.metadata.integration

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DepartmentSearchIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    // --- Name search ---

    @Test
    @Order(1)
    fun `search Meat returns Meat and Poultry (case-sensitive prefix)`() {
        RestAssured.given()
            .auth().basic("user", "password")
            .queryParam("q", "Meat")
            .get("/api/v1/department/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.name", hasItem("Meat & Poultry"))
    }

    @Test
    @Order(2)
    fun `search gRoCery returns Grocery case-insensitively`() {
        RestAssured.given()
            .auth().basic("user", "password")
            .queryParam("q", "gRoCery")
            .get("/api/v1/department/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.name", hasItem("Grocery"))
    }

    @Test
    @Order(3)
    fun `search dry returns Grocery and Health and Beauty`() {
        val names = RestAssured.given()
            .auth().basic("user", "password")
            .queryParam("q", "dry")
            .get("/api/v1/department/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path<List<String>>("data.name")

        // "Grocery (Dry Goods)" matches name; "Health & Beauty & dry (HBC)" matches name
        assert(names.any { it.contains("Grocery", ignoreCase = true) }) {
            "Expected Grocery in results but got: $names"
        }
        assert(names.any { it.contains("Health", ignoreCase = true) }) {
            "Expected Health & Beauty in results but got: $names"
        }
    }

    // --- Description search ---

    @Test
    @Order(4)
    fun `search ice in description returns Frozen Foods and Beverages`() {
        val names = RestAssured.given()
            .auth().basic("user", "password")
            .queryParam("q", "ice")
            .get("/api/v1/department/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path<List<String>>("data.name")

        // "Ice cream" in Frozen Foods; "ice beer" in Beverages
        assert(names.any { it.contains("Frozen", ignoreCase = true) }) {
            "Expected Frozen Foods in results but got: $names"
        }
        assert(names.any { it.contains("Beverage", ignoreCase = true) }) {
            "Expected Beverages in results but got: $names"
        }
    }

    @Test
    @Order(5)
    fun `search fresh in description returns Produce, Meat and Poultry, Seafood`() {
        val names = RestAssured.given()
            .auth().basic("user", "password")
            .queryParam("q", "fresh")
            .get("/api/v1/department/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path<List<String>>("data.name")

        assert(names.any { it.contains("Produce", ignoreCase = true) }) {
            "Expected Produce in results but got: $names"
        }
        assert(names.any { it.contains("Meat", ignoreCase = true) }) {
            "Expected Meat & Poultry in results but got: $names"
        }
        assert(names.any { it.contains("Seafood", ignoreCase = true) }) {
            "Expected Seafood in results but got: $names"
        }
    }

    @Test
    @Order(6)
    fun `search with blank query returns 400`() {
        RestAssured.given()
            .auth().basic("user", "password")
            .queryParam("q", "   ")
            .get("/api/v1/department/search")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    @Order(7)
    fun `search returns empty list for no matches`() {
        RestAssured.given()
            .auth().basic("user", "password")
            .queryParam("q", "xyznonexistent")
            .get("/api/v1/department/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", empty<Any>())
    }

    @Test
    @Order(8)
    fun `search endpoint requires authentication`() {
        RestAssured.given()
            .queryParam("q", "Meat")
            .get("/api/v1/department/search")
            .then()
            .statusCode(anyOf(equalTo(401), equalTo(403)))
    }
}
