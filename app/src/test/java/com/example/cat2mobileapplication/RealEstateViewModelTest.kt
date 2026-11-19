package com.example.cat2mobileapplication

import com.example.cat2mobileapplication.data.local.dao.LeaseDao
import com.example.cat2mobileapplication.data.local.dao.PropertyDao
import com.example.cat2mobileapplication.data.local.dao.RentPaymentDao
import com.example.cat2mobileapplication.data.local.dao.TenantDao
import com.example.cat2mobileapplication.data.model.Lease
import com.example.cat2mobileapplication.data.model.PaymentStatus
import com.example.cat2mobileapplication.data.model.Property
import com.example.cat2mobileapplication.data.model.PropertyStatus
import com.example.cat2mobileapplication.data.model.PropertyType
import com.example.cat2mobileapplication.data.model.RentPayment
import com.example.cat2mobileapplication.data.model.Tenant
import com.example.cat2mobileapplication.data.model.TenantStatus
import com.example.cat2mobileapplication.data.repository.RealEstateRepository
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RealEstateViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private class FakePropertyDao : PropertyDao {
        private val list = MutableStateFlow<List<Property>>(emptyList())
        override fun getAllProperties(): Flow<List<Property>> = list
        override suspend fun getPropertyById(id: String): Property? = list.value.find { it.id == id }
        override suspend fun insertProperty(property: Property) { list.value = list.value + property }
        override suspend fun insertProperties(properties: List<Property>) { list.value = list.value + properties }
        override suspend fun updateProperty(property: Property) { list.value = list.value.map { if (it.id == property.id) property else it } }
        override suspend fun deleteProperty(property: Property) { list.value = list.value.filter { it.id != property.id } }
        override suspend fun deletePropertyById(id: String) { list.value = list.value.filter { it.id != id } }
    }

    private class FakeTenantDao : TenantDao {
        private val list = MutableStateFlow<List<Tenant>>(emptyList())
        override fun getAllTenants(): Flow<List<Tenant>> = list
        override suspend fun getTenantById(id: String): Tenant? = list.value.find { it.id == id }
        override suspend fun insertTenant(tenant: Tenant) { list.value = list.value + tenant }
        override suspend fun insertTenants(tenants: List<Tenant>) { list.value = list.value + tenants }
        override suspend fun updateTenant(tenant: Tenant) { list.value = list.value.map { if (it.id == tenant.id) tenant else it } }
        override suspend fun deleteTenant(tenant: Tenant) { list.value = list.value.filter { it.id != tenant.id } }
        override suspend fun deleteTenantById(id: String) { list.value = list.value.filter { it.id != id } }
    }

    private class FakeLeaseDao : LeaseDao {
        private val list = MutableStateFlow<List<Lease>>(emptyList())
        override fun getAllLeases(): Flow<List<Lease>> = list
        override suspend fun getLeaseById(id: String): Lease? = list.value.find { it.id == id }
        override suspend fun insertLease(lease: Lease) { list.value = list.value + lease }
        override suspend fun insertLeases(leases: List<Lease>) { list.value = list.value + leases }
        override suspend fun updateLease(lease: Lease) { list.value = list.value.map { if (it.id == lease.id) lease else it } }
        override suspend fun deleteLease(lease: Lease) { list.value = list.value.filter { it.id != lease.id } }
        override suspend fun deleteLeaseById(id: String) { list.value = list.value.filter { it.id != id } }
    }

    private class FakeRentPaymentDao : RentPaymentDao {
        private val list = MutableStateFlow<List<RentPayment>>(emptyList())
        override fun getAllRentPayments(): Flow<List<RentPayment>> = list
        override suspend fun getRentPaymentById(id: String): RentPayment? = list.value.find { it.id == id }
        override suspend fun insertRentPayment(rentPayment: RentPayment) { list.value = list.value + rentPayment }
        override suspend fun insertRentPayments(rentPayments: List<RentPayment>) { list.value = list.value + rentPayments }
        override suspend fun updateRentPayment(rentPayment: RentPayment) { list.value = list.value.map { if (it.id == rentPayment.id) rentPayment else it } }
        override suspend fun updateRentStatus(id: String, status: PaymentStatus, paidDate: String?) {
            list.value = list.value.map { if (it.id == id) it.copy(status = status, paidDate = paidDate) else it }
        }
        override suspend fun deleteRentPayment(rentPayment: RentPayment) { list.value = list.value.filter { it.id != rentPayment.id } }
        override suspend fun deleteRentPaymentById(id: String) { list.value = list.value.filter { it.id != id } }
    }

    private lateinit var repository: RealEstateRepository
    private lateinit var viewModel: RealEstateViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = RealEstateRepository(
            propertyDao = FakePropertyDao(),
            tenantDao = FakeTenantDao(),
            rentPaymentDao = FakeRentPaymentDao(),
            leaseDao = FakeLeaseDao()
        )
        viewModel = RealEstateViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testRepositoryPropertyCrudFlow() = runTest {
        val property = Property(
            id = "p_vm1",
            title = "Penthouse Suite",
            address = "100 Skyline Blvd",
            type = PropertyType.CONDO,
            monthlyRent = 8000.0,
            bedrooms = 3,
            bathrooms = 3,
            status = PropertyStatus.AVAILABLE
        )

        // Create
        repository.addProperty(property)
        val list1 = repository.properties.first()
        assertEquals(1, list1.size)
        assertEquals("Penthouse Suite", list1[0].title)

        // Update
        val updated = property.copy(status = PropertyStatus.RENTED)
        repository.updateProperty(updated)
        val list2 = repository.properties.first()
        assertEquals(PropertyStatus.RENTED, list2[0].status)

        // Delete
        repository.deleteProperty("p_vm1")
        val list3 = repository.properties.first()
        assertTrue(list3.isEmpty())
    }

    @Test
    fun testRepositoryTenantCrudFlow() = runTest {
        val tenant = Tenant(
            id = "t_vm1",
            name = "Jane Smith",
            email = "jane@example.com",
            phone = "+1 (555) 333-4444",
            propertyId = "p_vm1",
            leaseStart = "2025-01-01",
            leaseEnd = "2026-01-01",
            status = TenantStatus.ACTIVE
        )

        // Create
        repository.addTenant(tenant)
        val list1 = repository.tenants.first()
        assertEquals(1, list1.size)

        // Update
        val updated = tenant.copy(status = TenantStatus.FORMER)
        repository.updateTenant(updated)
        val list2 = repository.tenants.first()
        assertEquals(TenantStatus.FORMER, list2[0].status)

        // Delete
        repository.deleteTenant("t_vm1")
        val list3 = repository.tenants.first()
        assertTrue(list3.isEmpty())
    }
}
