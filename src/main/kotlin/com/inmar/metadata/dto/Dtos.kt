package com.inmar.metadata.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

// ---- Request DTOs ----

data class LocationRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null
)

data class DepartmentRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null
)

data class CategoryRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null
)

data class SubcategoryRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null
)

data class MetadataSearchRequest(
    val location: String? = null,
    val department: String? = null,
    val category: String? = null,
    val subcategory: String? = null
)

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)

// ---- Response DTOs ----

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LocationResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DepartmentResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val locationId: Long,
    val locationName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val departmentId: Long,
    val departmentName: String,
    val locationId: Long,
    val locationName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SubcategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val categoryId: Long,
    val categoryName: String,
    val departmentId: Long,
    val departmentName: String,
    val locationId: Long,
    val locationName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SkuResponse(
    val id: Long,
    val skuCode: String,
    val name: String,
    val location: String?,
    val department: String?,
    val category: String?,
    val subcategory: String?
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val traceId: String? = null
)

data class LoginResponse(
    val token: String,
    val username: String,
    val role: String,
    val expiresIn: Long
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val traceId: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
