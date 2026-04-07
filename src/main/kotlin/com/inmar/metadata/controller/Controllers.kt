package com.inmar.metadata.controller

import com.inmar.metadata.dto.*
import com.inmar.metadata.service.*
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val BASE = "/api/v1"

@RestController
@RequestMapping("$BASE/location")
class LocationController(private val service: LocationService) {

    private val log = LoggerFactory.getLogger(LocationController::class.java)

    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<LocationResponse>>> {
        log.info("GET /api/v1/location")
        return ResponseEntity.ok(ApiResponse(true, data = service.getAll()))
    }

    @GetMapping("/{locationId}")
    fun getById(@PathVariable locationId: Long): ResponseEntity<ApiResponse<LocationResponse>> {
        return ResponseEntity.ok(ApiResponse(true, data = service.getById(locationId)))
    }

    @PostMapping
    fun create(@Valid @RequestBody req: LocationRequest): ResponseEntity<ApiResponse<LocationResponse>> {
        log.info("POST /api/v1/location name={}", req.name)
        val created = service.create(req)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(true, "Location created", created))
    }

    @PutMapping("/{locationId}")
    fun update(
        @PathVariable locationId: Long,
        @Valid @RequestBody req: LocationRequest
    ): ResponseEntity<ApiResponse<LocationResponse>> {
        return ResponseEntity.ok(ApiResponse(true, "Location updated", service.update(locationId, req)))
    }

    @DeleteMapping("/{locationId}")
    fun delete(@PathVariable locationId: Long): ResponseEntity<ApiResponse<Nothing>> {
        service.delete(locationId)
        return ResponseEntity.ok(ApiResponse(true, "Location deleted"))
    }
}

@RestController
@RequestMapping("$BASE/department")
class DepartmentSearchController(private val service: DepartmentService) {

    private val log = LoggerFactory.getLogger(DepartmentSearchController::class.java)

    @GetMapping("/search")
    fun search(@RequestParam q: String): ResponseEntity<ApiResponse<List<DepartmentResponse>>> {
        log.info("GET /api/v1/department/search q=\"{}\"", q)
        return ResponseEntity.ok(ApiResponse(true, data = service.search(q)))
    }
}

@RestController
@RequestMapping("$BASE/location/{locationId}/department")
class DepartmentController(private val service: DepartmentService) {

    private val log = LoggerFactory.getLogger(DepartmentController::class.java)

    @GetMapping
    fun getAll(@PathVariable locationId: Long): ResponseEntity<ApiResponse<List<DepartmentResponse>>> {
        log.info("GET departments for locationId={}", locationId)
        return ResponseEntity.ok(ApiResponse(true, data = service.getByLocation(locationId)))
    }

    @GetMapping("/{departmentId}")
    fun getById(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long
    ): ResponseEntity<ApiResponse<DepartmentResponse>> {
        return ResponseEntity.ok(ApiResponse(true, data = service.getById(locationId, departmentId)))
    }

    @PostMapping
    fun create(
        @PathVariable locationId: Long,
        @Valid @RequestBody req: DepartmentRequest
    ): ResponseEntity<ApiResponse<DepartmentResponse>> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Department created", service.create(locationId, req)))
    }

    @PutMapping("/{departmentId}")
    fun update(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @Valid @RequestBody req: DepartmentRequest
    ): ResponseEntity<ApiResponse<DepartmentResponse>> {
        return ResponseEntity.ok(ApiResponse(true, "Department updated", service.update(locationId, departmentId, req)))
    }

    @DeleteMapping("/{departmentId}")
    fun delete(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long
    ): ResponseEntity<ApiResponse<Nothing>> {
        service.delete(locationId, departmentId)
        return ResponseEntity.ok(ApiResponse(true, "Department deleted"))
    }
}

@RestController
@RequestMapping("$BASE/location/{locationId}/department/{departmentId}/category")
class CategoryController(private val service: CategoryService) {

    @GetMapping
    fun getAll(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long
    ): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        return ResponseEntity.ok(ApiResponse(true, data = service.getByLocationAndDepartment(locationId, departmentId)))
    }

    @GetMapping("/{categoryId}")
    fun getById(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        return ResponseEntity.ok(ApiResponse(true, data = service.getById(locationId, departmentId, categoryId)))
    }

    @PostMapping
    fun create(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @Valid @RequestBody req: CategoryRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Category created", service.create(locationId, departmentId, req)))
    }

    @PutMapping("/{categoryId}")
    fun update(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long,
        @Valid @RequestBody req: CategoryRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        return ResponseEntity.ok(ApiResponse(true, "Category updated", service.update(locationId, departmentId, categoryId, req)))
    }

    @DeleteMapping("/{categoryId}")
    fun delete(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long
    ): ResponseEntity<ApiResponse<Nothing>> {
        service.delete(locationId, departmentId, categoryId)
        return ResponseEntity.ok(ApiResponse(true, "Category deleted"))
    }
}

@RestController
@RequestMapping("$BASE/location/{locationId}/department/{departmentId}/category/{categoryId}/subcategory")
class SubcategoryController(private val service: SubcategoryService) {

    @GetMapping
    fun getAll(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long
    ): ResponseEntity<ApiResponse<List<SubcategoryResponse>>> {
        return ResponseEntity.ok(ApiResponse(true, data = service.getAll(locationId, departmentId, categoryId)))
    }

    @GetMapping("/{subcategoryId}")
    fun getById(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long,
        @PathVariable subcategoryId: Long
    ): ResponseEntity<ApiResponse<SubcategoryResponse>> {
        return ResponseEntity.ok(ApiResponse(true, data = service.getById(locationId, departmentId, categoryId, subcategoryId)))
    }

    @PostMapping
    fun create(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long,
        @Valid @RequestBody req: SubcategoryRequest
    ): ResponseEntity<ApiResponse<SubcategoryResponse>> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Subcategory created", service.create(locationId, departmentId, categoryId, req)))
    }

    @PutMapping("/{subcategoryId}")
    fun update(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long,
        @PathVariable subcategoryId: Long,
        @Valid @RequestBody req: SubcategoryRequest
    ): ResponseEntity<ApiResponse<SubcategoryResponse>> {
        return ResponseEntity.ok(ApiResponse(true, "Subcategory updated",
            service.update(locationId, departmentId, categoryId, subcategoryId, req)))
    }

    @DeleteMapping("/{subcategoryId}")
    fun delete(
        @PathVariable locationId: Long,
        @PathVariable departmentId: Long,
        @PathVariable categoryId: Long,
        @PathVariable subcategoryId: Long
    ): ResponseEntity<ApiResponse<Nothing>> {
        service.delete(locationId, departmentId, categoryId, subcategoryId)
        return ResponseEntity.ok(ApiResponse(true, "Subcategory deleted"))
    }
}

@RestController
@RequestMapping("$BASE/sku")
class SkuController(private val service: SkuService) {

    private val log = LoggerFactory.getLogger(SkuController::class.java)

    @PostMapping("/search")
    fun searchByMetadata(@RequestBody req: MetadataSearchRequest): ResponseEntity<ApiResponse<List<SkuResponse>>> {
        log.info("POST /api/v1/sku/search with metadata={}", req)
        return ResponseEntity.ok(ApiResponse(true, data = service.searchByMetadata(req)))
    }

    @GetMapping("/search")
    fun searchByMetadataGet(
        @RequestParam(required = false) location: String?,
        @RequestParam(required = false) department: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) subcategory: String?
    ): ResponseEntity<ApiResponse<List<SkuResponse>>> {
        val req = MetadataSearchRequest(location, department, category, subcategory)
        log.info("GET /api/v1/sku/search with metadata={}", req)
        return ResponseEntity.ok(ApiResponse(true, data = service.searchByMetadata(req)))
    }
}
