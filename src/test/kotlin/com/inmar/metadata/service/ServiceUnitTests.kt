package com.inmar.metadata.service

import com.inmar.metadata.dto.LocationRequest
import com.inmar.metadata.dto.MetadataSearchRequest
import com.inmar.metadata.entity.*
import com.inmar.metadata.repository.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.Optional

class LocationServiceTest {

    private val locationRepo: LocationRepository = mock()
    private val service = LocationService(locationRepo)

    private fun makeLocation(id: Long = 1L, name: String = "Perimeter") = Location(
        id = id, name = name, description = "desc",
        createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
    )

    @Test
    fun `getAll returns mapped responses`() {
        whenever(locationRepo.findAll()).thenReturn(listOf(makeLocation(1, "Perimeter"), makeLocation(2, "Center")))
        val result = service.getAll()
        assertEquals(2, result.size)
        assertEquals("Perimeter", result[0].name)
        assertEquals("Center", result[1].name)
    }

    @Test
    fun `getById returns location when found`() {
        whenever(locationRepo.findById(1L)).thenReturn(Optional.of(makeLocation()))
        val result = service.getById(1L)
        assertEquals("Perimeter", result.name)
    }

    @Test
    fun `getById throws NotFoundException when not found`() {
        whenever(locationRepo.findById(99L)).thenReturn(Optional.empty())
        assertThrows<NotFoundException> { service.getById(99L) }
    }

    @Test
    fun `create saves and returns response`() {
        val req = LocationRequest("NewLocation", "desc")
        val saved = makeLocation(5L, "NewLocation")
        whenever(locationRepo.existsByName("NewLocation")).thenReturn(false)
        whenever(locationRepo.save(any())).thenReturn(saved)
        val result = service.create(req)
        assertEquals("NewLocation", result.name)
        verify(locationRepo).save(any())
    }

    @Test
    fun `create throws ConflictException for duplicate name`() {
        whenever(locationRepo.existsByName("Perimeter")).thenReturn(true)
        assertThrows<ConflictException> { service.create(LocationRequest("Perimeter")) }
        verify(locationRepo, never()).save(any())
    }

    @Test
    fun `update throws NotFoundException when location does not exist`() {
        whenever(locationRepo.findById(99L)).thenReturn(Optional.empty())
        assertThrows<NotFoundException> { service.update(99L, LocationRequest("X")) }
    }

    @Test
    fun `delete throws NotFoundException when location does not exist`() {
        whenever(locationRepo.existsById(99L)).thenReturn(false)
        assertThrows<NotFoundException> { service.delete(99L) }
        verify(locationRepo, never()).deleteById(any())
    }

    @Test
    fun `delete calls deleteById when found`() {
        whenever(locationRepo.existsById(1L)).thenReturn(true)
        service.delete(1L)
        verify(locationRepo).deleteById(1L)
    }
}

class SkuServiceTest {

    private val skuRepo: SkuRepository = mock()
    private val service = SkuService(skuRepo)

    private fun makeLocation(name: String) = Location(1L, name, null, LocalDateTime.now(), LocalDateTime.now())
    private fun makeDept(name: String, loc: Location) = Department(1L, name, null, loc, LocalDateTime.now(), LocalDateTime.now())
    private fun makeCat(name: String, dept: Department) = Category(1L, name, null, dept, LocalDateTime.now(), LocalDateTime.now())
    private fun makeSub(name: String, cat: Category) = Subcategory(1L, name, null, cat, LocalDateTime.now(), LocalDateTime.now())

    private fun makeSku(code: String, loc: String, dept: String, cat: String, sub: String): Sku {
        val location = makeLocation(loc)
        val department = makeDept(dept, location)
        val category = makeCat(cat, department)
        val subcategory = makeSub(sub, category)
        return Sku(1L, code, "SKU$code", location, department, category, subcategory)
    }

    @Test
    fun `searchByMetadata delegates to repository and maps results`() {
        val sku1 = makeSku("1", "Perimeter", "Bakery", "Bakery Bread", "Bagels")
        val sku14 = makeSku("14", "Perimeter", "Bakery", "Bakery Bread", "Bagels")
        val req = MetadataSearchRequest("Perimeter", "Bakery", "Bakery Bread", "Bagels")
        whenever(skuRepo.findByMetadata("Perimeter", "Bakery", "Bakery Bread", "Bagels"))
            .thenReturn(listOf(sku1, sku14))

        val result = service.searchByMetadata(req)
        assertEquals(2, result.size)
        assertEquals("1", result[0].skuCode)
        assertEquals("14", result[1].skuCode)
        assertEquals("Perimeter", result[0].location)
        assertEquals("Bagels", result[0].subcategory)
    }

    @Test
    fun `searchByMetadata with no filters returns all`() {
        val req = MetadataSearchRequest()
        whenever(skuRepo.findByMetadata(null, null, null, null)).thenReturn(emptyList())
        val result = service.searchByMetadata(req)
        assertTrue(result.isEmpty())
        verify(skuRepo).findByMetadata(null, null, null, null)
    }

    @Test
    fun `searchByMetadata partial filters are passed correctly`() {
        val req = MetadataSearchRequest(location = "Center", department = "Frozen")
        whenever(skuRepo.findByMetadata("Center", "Frozen", null, null)).thenReturn(emptyList())
        service.searchByMetadata(req)
        verify(skuRepo).findByMetadata("Center", "Frozen", null, null)
    }
}

class DepartmentServiceTest {

    private val departmentRepo: DepartmentRepository = mock()
    private val locationRepo: LocationRepository = mock()
    private val service = DepartmentService(departmentRepo, locationRepo)

    private fun makeLocation(id: Long = 1L) =
        Location(id, "Perimeter", null, LocalDateTime.now(), LocalDateTime.now())

    private fun makeDept(id: Long = 1L, loc: Location) =
        Department(id, "Bakery", "desc", loc, LocalDateTime.now(), LocalDateTime.now())

    @Test
    fun `getByLocation throws when location not found`() {
        whenever(locationRepo.existsById(99L)).thenReturn(false)
        assertThrows<NotFoundException> { service.getByLocation(99L) }
    }

    @Test
    fun `getByLocation returns departments`() {
        val loc = makeLocation()
        whenever(locationRepo.existsById(1L)).thenReturn(true)
        whenever(departmentRepo.findByLocationId(1L)).thenReturn(listOf(makeDept(loc = loc)))
        val result = service.getByLocation(1L)
        assertEquals(1, result.size)
        assertEquals("Bakery", result[0].name)
    }

    @Test
    fun `create throws ConflictException for duplicate`() {
        whenever(locationRepo.findById(1L)).thenReturn(Optional.of(makeLocation()))
        whenever(departmentRepo.existsByNameAndLocationId("Bakery", 1L)).thenReturn(true)
        assertThrows<ConflictException> { service.create(1L, com.inmar.metadata.dto.DepartmentRequest("Bakery")) }
    }
}
