package com.inmar.metadata.service

import com.inmar.metadata.dto.*
import com.inmar.metadata.entity.*
import com.inmar.metadata.repository.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LocationService(private val locationRepo: LocationRepository) {

    private val log = LoggerFactory.getLogger(LocationService::class.java)

    fun getAll(): List<LocationResponse> {
        log.debug("Fetching all locations")
        return locationRepo.findAll().map { it.toResponse() }
    }

    fun getById(id: Long): LocationResponse {
        log.debug("Fetching location id={}", id)
        return locationRepo.findById(id)
            .orElseThrow { NotFoundException("Location not found: $id") }
            .toResponse()
    }

    fun create(req: LocationRequest): LocationResponse {
        log.info("Creating location name={}", req.name)
        if (locationRepo.existsByName(req.name))
            throw ConflictException("Location '${req.name}' already exists")
        return locationRepo.save(Location(name = req.name.trim(), description = req.description?.trim())).toResponse()
    }

    fun update(id: Long, req: LocationRequest): LocationResponse {
        log.info("Updating location id={}", id)
        val existing = locationRepo.findById(id).orElseThrow { NotFoundException("Location not found: $id") }
        if (existing.name != req.name && locationRepo.existsByName(req.name))
            throw ConflictException("Location '${req.name}' already exists")
        val updated = existing.copy(name = req.name.trim(), description = req.description?.trim())
        return locationRepo.save(updated).toResponse()
    }

    fun delete(id: Long) {
        log.info("Deleting location id={}", id)
        if (!locationRepo.existsById(id)) throw NotFoundException("Location not found: $id")
        locationRepo.deleteById(id)
    }

    private fun Location.toResponse() = LocationResponse(id, name, description, createdAt, updatedAt)
}

@Service
@Transactional
class DepartmentService(
    private val departmentRepo: DepartmentRepository,
    private val locationRepo: LocationRepository
) {
    private val log = LoggerFactory.getLogger(DepartmentService::class.java)

    fun getByLocation(locationId: Long): List<DepartmentResponse> {
        log.debug("Fetching departments for locationId={}", locationId)
        ensureLocationExists(locationId)
        return departmentRepo.findByLocationId(locationId).map { it.toResponse() }
    }

    fun getById(locationId: Long, departmentId: Long): DepartmentResponse {
        ensureLocationExists(locationId)
        return departmentRepo.findByIdAndLocationId(departmentId, locationId)
            .orElseThrow { NotFoundException("Department $departmentId not found in location $locationId") }
            .toResponse()
    }

    fun create(locationId: Long, req: DepartmentRequest): DepartmentResponse {
        log.info("Creating department name={} locationId={}", req.name, locationId)
        val location = locationRepo.findById(locationId).orElseThrow { NotFoundException("Location not found: $locationId") }
        if (departmentRepo.existsByNameAndLocationId(req.name, locationId))
            throw ConflictException("Department '${req.name}' already exists in this location")
        return departmentRepo.save(Department(name = req.name.trim(), description = req.description?.trim(), location = location)).toResponse()
    }

    fun update(locationId: Long, departmentId: Long, req: DepartmentRequest): DepartmentResponse {
        val location = locationRepo.findById(locationId).orElseThrow { NotFoundException("Location not found: $locationId") }
        val existing = departmentRepo.findByIdAndLocationId(departmentId, locationId)
            .orElseThrow { NotFoundException("Department $departmentId not found in location $locationId") }
        val updated = existing.copy(name = req.name.trim(), description = req.description?.trim())
        return departmentRepo.save(updated).toResponse()
    }

    fun delete(locationId: Long, departmentId: Long) {
        ensureLocationExists(locationId)
        val dep = departmentRepo.findByIdAndLocationId(departmentId, locationId)
            .orElseThrow { NotFoundException("Department $departmentId not found in location $locationId") }
        departmentRepo.delete(dep)
    }

    fun search(query: String): List<DepartmentResponse> {
        log.info("Searching departments query=\"{}\"", query)
        if (query.isBlank()) throw BadRequestException("Search query must not be blank")
        return departmentRepo.searchByNameOrDescription(query.trim()).map { it.toResponse() }
    }

    private fun ensureLocationExists(locationId: Long) {
        if (!locationRepo.existsById(locationId)) throw NotFoundException("Location not found: $locationId")
    }

    private fun Department.toResponse() = DepartmentResponse(
        id, name, description, location.id, location.name, createdAt, updatedAt
    )
}

@Service
@Transactional
class CategoryService(
    private val categoryRepo: CategoryRepository,
    private val departmentRepo: DepartmentRepository,
    private val locationRepo: LocationRepository
) {
    private val log = LoggerFactory.getLogger(CategoryService::class.java)

    fun getByLocationAndDepartment(locationId: Long, departmentId: Long): List<CategoryResponse> {
        ensureExists(locationId, departmentId)
        return categoryRepo.findByLocationIdAndDepartmentId(locationId, departmentId).map { it.toResponse() }
    }

    fun getById(locationId: Long, departmentId: Long, categoryId: Long): CategoryResponse {
        ensureExists(locationId, departmentId)
        return categoryRepo.findByIdAndDepartmentIdAndLocationId(categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Category $categoryId not found") }
            .toResponse()
    }

    fun create(locationId: Long, departmentId: Long, req: CategoryRequest): CategoryResponse {
        log.info("Creating category name={} departmentId={}", req.name, departmentId)
        ensureExists(locationId, departmentId)
        val department = departmentRepo.findByIdAndLocationId(departmentId, locationId)
            .orElseThrow { NotFoundException("Department not found") }
        return categoryRepo.save(Category(name = req.name.trim(), description = req.description?.trim(), department = department)).toResponse()
    }

    fun update(locationId: Long, departmentId: Long, categoryId: Long, req: CategoryRequest): CategoryResponse {
        ensureExists(locationId, departmentId)
        val existing = categoryRepo.findByIdAndDepartmentIdAndLocationId(categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Category not found") }
        return categoryRepo.save(existing.copy(name = req.name.trim(), description = req.description?.trim())).toResponse()
    }

    fun delete(locationId: Long, departmentId: Long, categoryId: Long) {
        ensureExists(locationId, departmentId)
        val cat = categoryRepo.findByIdAndDepartmentIdAndLocationId(categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Category not found") }
        categoryRepo.delete(cat)
    }

    private fun ensureExists(locationId: Long, departmentId: Long) {
        if (!locationRepo.existsById(locationId)) throw NotFoundException("Location not found: $locationId")
        departmentRepo.findByIdAndLocationId(departmentId, locationId)
            .orElseThrow { NotFoundException("Department $departmentId not found in location $locationId") }
    }

    private fun Category.toResponse() = CategoryResponse(
        id, name, description,
        department.id, department.name,
        department.location.id, department.location.name,
        createdAt, updatedAt
    )
}

@Service
@Transactional
class SubcategoryService(
    private val subcategoryRepo: SubcategoryRepository,
    private val categoryRepo: CategoryRepository,
    private val departmentRepo: DepartmentRepository,
    private val locationRepo: LocationRepository
) {
    private val log = LoggerFactory.getLogger(SubcategoryService::class.java)

    fun getAll(locationId: Long, departmentId: Long, categoryId: Long): List<SubcategoryResponse> {
        ensureExists(locationId, departmentId, categoryId)
        return subcategoryRepo.findByLocationIdAndDepartmentIdAndCategoryId(locationId, departmentId, categoryId)
            .map { it.toResponse() }
    }

    fun getById(locationId: Long, departmentId: Long, categoryId: Long, subcategoryId: Long): SubcategoryResponse {
        ensureExists(locationId, departmentId, categoryId)
        return subcategoryRepo.findByIdAndCategoryIdAndDepartmentIdAndLocationId(subcategoryId, categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Subcategory not found: $subcategoryId") }
            .toResponse()
    }

    fun create(locationId: Long, departmentId: Long, categoryId: Long, req: SubcategoryRequest): SubcategoryResponse {
        log.info("Creating subcategory name={} categoryId={}", req.name, categoryId)
        ensureExists(locationId, departmentId, categoryId)
        val category = categoryRepo.findByIdAndDepartmentIdAndLocationId(categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Category not found") }
        return subcategoryRepo.save(Subcategory(name = req.name.trim(), description = req.description?.trim(), category = category)).toResponse()
    }

    fun update(locationId: Long, departmentId: Long, categoryId: Long, subcategoryId: Long, req: SubcategoryRequest): SubcategoryResponse {
        ensureExists(locationId, departmentId, categoryId)
        val existing = subcategoryRepo.findByIdAndCategoryIdAndDepartmentIdAndLocationId(subcategoryId, categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Subcategory not found") }
        return subcategoryRepo.save(existing.copy(name = req.name.trim(), description = req.description?.trim())).toResponse()
    }

    fun delete(locationId: Long, departmentId: Long, categoryId: Long, subcategoryId: Long) {
        ensureExists(locationId, departmentId, categoryId)
        val sub = subcategoryRepo.findByIdAndCategoryIdAndDepartmentIdAndLocationId(subcategoryId, categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Subcategory not found") }
        subcategoryRepo.delete(sub)
    }

    private fun ensureExists(locationId: Long, departmentId: Long, categoryId: Long) {
        if (!locationRepo.existsById(locationId)) throw NotFoundException("Location not found: $locationId")
        departmentRepo.findByIdAndLocationId(departmentId, locationId)
            .orElseThrow { NotFoundException("Department not found: $departmentId") }
        categoryRepo.findByIdAndDepartmentIdAndLocationId(categoryId, departmentId, locationId)
            .orElseThrow { NotFoundException("Category not found: $categoryId") }
    }

    private fun Subcategory.toResponse() = SubcategoryResponse(
        id, name, description,
        category.id, category.name,
        category.department.id, category.department.name,
        category.department.location.id, category.department.location.name,
        createdAt, updatedAt
    )
}

@Service
@Transactional(readOnly = true)
class SkuService(private val skuRepo: SkuRepository) {
    private val log = LoggerFactory.getLogger(SkuService::class.java)

    fun searchByMetadata(req: MetadataSearchRequest): List<SkuResponse> {
        log.info("Searching SKUs: location={}, department={}, category={}, subcategory={}",
            req.location, req.department, req.category, req.subcategory)
        return skuRepo.findByMetadata(req.location, req.department, req.category, req.subcategory)
            .map { it.toResponse() }
    }

    private fun Sku.toResponse() = SkuResponse(
        id = id, skuCode = skuCode, name = name,
        location = location?.name,
        department = department?.name,
        category = category?.name,
        subcategory = subcategory?.name
    )
}

// ---- Custom Exceptions ----
class NotFoundException(message: String) : RuntimeException(message)
class ConflictException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)
