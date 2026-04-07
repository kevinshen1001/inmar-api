package com.inmar.metadata.repository

import com.inmar.metadata.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface LocationRepository : JpaRepository<Location, Long> {
    fun findByName(name: String): Optional<Location>
    fun existsByName(name: String): Boolean
}

@Repository
interface DepartmentRepository : JpaRepository<Department, Long> {
    fun findByLocationId(locationId: Long): List<Department>
    fun findByIdAndLocationId(id: Long, locationId: Long): Optional<Department>
    fun existsByNameAndLocationId(name: String, locationId: Long): Boolean

    @Query("""
        SELECT d FROM Department d
        WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(COALESCE(d.description, '')) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY d.name
    """)
    fun searchByNameOrDescription(@Param("query") query: String): List<Department>
}

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByDepartmentId(departmentId: Long): List<Category>
    fun findByIdAndDepartmentId(id: Long, departmentId: Long): Optional<Category>

    @Query("""
        SELECT c FROM Category c
        JOIN c.department d
        WHERE d.id = :departmentId AND d.location.id = :locationId
    """)
    fun findByLocationIdAndDepartmentId(
        @Param("locationId") locationId: Long,
        @Param("departmentId") departmentId: Long
    ): List<Category>

    @Query("""
        SELECT c FROM Category c
        JOIN c.department d
        WHERE c.id = :categoryId AND d.id = :departmentId AND d.location.id = :locationId
    """)
    fun findByIdAndDepartmentIdAndLocationId(
        @Param("categoryId") categoryId: Long,
        @Param("departmentId") departmentId: Long,
        @Param("locationId") locationId: Long
    ): Optional<Category>
}

@Repository
interface SubcategoryRepository : JpaRepository<Subcategory, Long> {
    fun findByCategoryId(categoryId: Long): List<Subcategory>

    @Query("""
        SELECT s FROM Subcategory s
        JOIN s.category c
        JOIN c.department d
        WHERE c.id = :categoryId AND d.id = :departmentId AND d.location.id = :locationId
    """)
    fun findByLocationIdAndDepartmentIdAndCategoryId(
        @Param("locationId") locationId: Long,
        @Param("departmentId") departmentId: Long,
        @Param("categoryId") categoryId: Long
    ): List<Subcategory>

    @Query("""
        SELECT s FROM Subcategory s
        JOIN s.category c
        JOIN c.department d
        WHERE s.id = :subcategoryId AND c.id = :categoryId AND d.id = :departmentId AND d.location.id = :locationId
    """)
    fun findByIdAndCategoryIdAndDepartmentIdAndLocationId(
        @Param("subcategoryId") subcategoryId: Long,
        @Param("categoryId") categoryId: Long,
        @Param("departmentId") departmentId: Long,
        @Param("locationId") locationId: Long
    ): Optional<Subcategory>
}

@Repository
interface SkuRepository : JpaRepository<Sku, Long> {
    @Query("""
        SELECT s FROM Sku s
        WHERE (:locationName IS NULL OR LOWER(s.location.name) = LOWER(:locationName))
          AND (:departmentName IS NULL OR LOWER(s.department.name) = LOWER(:departmentName))
          AND (:categoryName IS NULL OR LOWER(s.category.name) = LOWER(:categoryName))
          AND (:subcategoryName IS NULL OR LOWER(s.subcategory.name) = LOWER(:subcategoryName))
    """)
    fun findByMetadata(
        @Param("locationName") locationName: String?,
        @Param("departmentName") departmentName: String?,
        @Param("categoryName") categoryName: String?,
        @Param("subcategoryName") subcategoryName: String?
    ): List<Sku>
}

@Repository
interface AppUserRepository : JpaRepository<AppUser, Long> {
    fun findByUsername(username: String): Optional<AppUser>
}
