package com.inmar.metadata.integration

import com.inmar.metadata.repository.DepartmentRepository
import com.inmar.metadata.repository.LocationRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CategoryControllerIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired private lateinit var locationRepo: LocationRepository
    @Autowired private lateinit var departmentRepo: DepartmentRepository

    private lateinit var adminToken: String
    private var locationId: Long = 0
    private var departmentId: Long = 0

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        adminToken = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"admin","password":"password"}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("data.token")

        locationId = locationRepo.findByName("Perimeter").get().id
        departmentId = departmentRepo.findByLocationId(locationId)
            .first { it.name == "Bakery" }.id
    }

    @Test
    @Order(1)
    fun `GET categories for location and department returns data`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", not(empty<Any>()))
            .body("data.name", hasItem("Bakery Bread"))
    }

    @Test
    @Order(2)
    fun `POST create category succeeds`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"Test Category","description":"Test"}""")
            .post("/api/v1/location/$locationId/department/$departmentId/category")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("data.name", equalTo("Test Category"))
            .body("data.departmentName", equalTo("Bakery"))
            .body("data.locationName", equalTo("Perimeter"))
    }

    @Test
    @Order(3)
    fun `GET category by id returns correct category`() {
        val categoryId = RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category")
            .then().extract().path<List<Map<String, Any>>>("data")
            .first { it["name"] == "Bakery Bread" }["id"] as Int

        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category/$categoryId")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.name", equalTo("Bakery Bread"))
    }

    @Test
    @Order(4)
    fun `DELETE category succeeds`() {
        val categoryId = RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category")
            .then().extract().path<List<Map<String, Any>>>("data")
            .first { it["name"] == "Test Category" }["id"] as Int

        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .delete("/api/v1/location/$locationId/department/$departmentId/category/$categoryId")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", equalTo(true))
    }
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SubcategoryControllerIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired private lateinit var locationRepo: LocationRepository
    @Autowired private lateinit var departmentRepo: DepartmentRepository

    private lateinit var adminToken: String
    private var locationId: Long = 0
    private var departmentId: Long = 0
    private var categoryId: Long = 0

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        adminToken = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"username":"admin","password":"password"}""")
            .post("/api/v1/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("data.token")

        locationId = locationRepo.findByName("Perimeter").get().id
        departmentId = departmentRepo.findByLocationId(locationId).first { it.name == "Bakery" }.id

        categoryId = (RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category")
            .then().extract().path<List<Map<String, Any>>>("data")
            .first { it["name"] == "Bakery Bread" }["id"] as Int).toLong()
    }

    @Test
    @Order(1)
    fun `GET subcategories returns data`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category/$categoryId/subcategory")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", not(empty<Any>()))
            .body("data.name", hasItem("Bagels"))
    }

    @Test
    @Order(2)
    fun `POST create subcategory succeeds`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"Test Subcategory"}""")
            .post("/api/v1/location/$locationId/department/$departmentId/category/$categoryId/subcategory")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("data.name", equalTo("Test Subcategory"))
            .body("data.categoryName", equalTo("Bakery Bread"))
    }

    @Test
    @Order(3)
    fun `GET subcategory by id works`() {
        val subcategoryId = RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category/$categoryId/subcategory")
            .then().extract().path<List<Map<String, Any>>>("data")
            .first { it["name"] == "Bagels" }["id"] as Int

        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category/$categoryId/subcategory/$subcategoryId")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.name", equalTo("Bagels"))
    }

    @Test
    @Order(4)
    fun `PUT update subcategory succeeds`() {
        val subcategoryId = RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category/$categoryId/subcategory")
            .then().extract().path<List<Map<String, Any>>>("data")
            .first { it["name"] == "Test Subcategory" }["id"] as Int

        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .contentType(ContentType.JSON)
            .body("""{"name":"Test Subcategory Updated"}""")
            .put("/api/v1/location/$locationId/department/$departmentId/category/$categoryId/subcategory/$subcategoryId")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.name", equalTo("Test Subcategory Updated"))
    }

    @Test
    @Order(5)
    fun `GET subcategory with wrong parent returns 404`() {
        RestAssured.given()
            .header("Authorization", "Bearer $adminToken")
            .get("/api/v1/location/$locationId/department/$departmentId/category/99999/subcategory")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }
}
