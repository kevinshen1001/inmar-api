package com.inmar.metadata.integration

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SkuSearchIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        token = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"user","password":"password"}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("data.token")
    }

    @Test
    @Order(1)
    fun `SKU search by all metadata fields returns correct SKUs`() {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body("""
                {
                  "location": "Perimeter",
                  "department": "Bakery",
                  "category": "Bakery Bread",
                  "subcategory": "Bagels"
                }
            """.trimIndent())
            .post("/api/v1/sku/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", not(empty<Any>()))
            .body("data.skuCode", hasItems("1", "14"))
    }

    @Test
    @Order(2)
    fun `SKU search by location only returns all SKUs in that location`() {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body("""{"location": "Perimeter"}""")
            .post("/api/v1/sku/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", not(empty<Any>()))
    }

    @Test
    @Order(3)
    fun `SKU search by department returns matching SKUs`() {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body("""{"department": "Seafood"}""")
            .post("/api/v1/sku/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.skuCode", hasItems("10", "11", "12", "13"))
    }

    @Test
    @Order(4)
    fun `SKU search with no matching results returns empty list`() {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body("""{"location": "NonExistentPlace"}""")
            .post("/api/v1/sku/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", empty<Any>())
    }

    @Test
    @Order(5)
    fun `SKU search via GET with query params works`() {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .queryParam("location", "Perimeter")
            .queryParam("department", "Bakery")
            .queryParam("category", "Bakery Bread")
            .queryParam("subcategory", "Bagels")
            .get("/api/v1/sku/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.skuCode", hasItems("1", "14"))
    }

    @Test
    @Order(6)
    fun `SKU search is case-insensitive`() {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body("""{"location":"PERIMETER","department":"bakery","category":"bakery bread","subcategory":"bagels"}""")
            .post("/api/v1/sku/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.skuCode", hasItems("1", "14"))
    }

    @Test
    @Order(7)
    fun `SKU search requires authentication`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"location":"Perimeter"}""")
            .post("/api/v1/sku/search")
            .then()
            .statusCode(anyOf(equalTo(401), equalTo(403)))
    }

    @Test
    @Order(8)
    fun `SKU search empty body returns all SKUs`() {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body("{}")
            .post("/api/v1/sku/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.size()", greaterThan(0))
    }
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @Test
    @Order(1)
    fun `login with valid admin credentials returns JWT`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"admin","password":"password"}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", equalTo(true))
            .body("data.token", notNullValue())
            .body("data.username", equalTo("admin"))
            .body("data.role", equalTo("ADMIN"))
    }

    @Test
    @Order(2)
    fun `login with invalid credentials returns 401`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"admin","password":"wrongpassword"}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    @Order(3)
    fun `login with blank password returns 400`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"admin","password":""}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    @Order(4)
    fun `accessing protected endpoint with expired token returns 401 or 403`() {
        val fakeToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MX0.invalid"
        RestAssured.given()
            .header("Authorization", "Bearer $fakeToken")
            .get("/api/v1/location")
            .then()
            .statusCode(anyOf(equalTo(401), equalTo(403)))
    }

    @Test
    @Order(5)
    fun `basic auth works for API access`() {
        RestAssured.given()
            .auth().basic("user", "password")
            .get("/api/v1/location")
            .then()
            .statusCode(HttpStatus.OK.value())
    }

    @Test
    @Order(6)
    fun `health endpoint is publicly accessible`() {
        RestAssured.given()
            .get("/actuator/health")
            .then()
            .statusCode(HttpStatus.OK.value())
    }
}
