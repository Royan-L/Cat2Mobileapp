package com.example.cat2mobileapplication

import com.example.cat2mobileapplication.data.local.dao.LeaseDao
import com.example.cat2mobileapplication.data.local.dao.PropertyDao
import com.example.cat2mobileapplication.data.local.dao.RentPaymentDao
import com.example.cat2mobileapplication.data.local.dao.TenantDao
import com.example.cat2mobileapplication.data.model.Lease
import com.example.cat2mobileapplication.data.model.LeaseStatus
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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

    @Test
    fun testViewModelPropertyManagement() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.properties.collect {}
        }

        val prop = Property(
            id = "p_test_vm",
            title = "Ocean View Villa",
            address = "500 Shoreline Ave",
            type = PropertyType.HOUSE,
            monthlyRent = 4500.0,
            bedrooms = 4,
            bathrooms = 3,
            status = PropertyStatus.AVAILABLE
        )

        viewModel.addProperty(prop)
        assertEquals(1, viewModel.properties.value.size)
        assertEquals("Ocean View Villa", viewModel.properties.value[0].title)

        val updatedProp = prop.copy(title = "Ocean View Luxury Villa", status = PropertyStatus.RENTED)
        viewModel.updateProperty(updatedProp)
        assertEquals("Ocean View Luxury Villa", viewModel.properties.value[0].title)
        assertEquals(PropertyStatus.RENTED, viewModel.properties.value[0].status)

        viewModel.deleteProperty("p_test_vm")
        assertTrue(viewModel.properties.value.isEmpty())
        collectJob.cancel()
    }

    @Test
    fun testViewModelTenantManagement() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tenants.collect {}
        }

        val tenant = Tenant(
            id = "t_test_vm",
            name = "Alex Mercer",
            email = "alex@example.com",
            phone = "+1 555-0199",
            propertyId = "p1",
            leaseStart = "2025-01-01",
            leaseEnd = "2026-01-01",
            status = TenantStatus.ACTIVE
        )

        viewModel.addTenant(tenant)
        assertEquals(1, viewModel.tenants.value.size)

        val updatedTenant = tenant.copy(status = TenantStatus.FORMER)
        viewModel.updateTenant(updatedTenant)
        assertEquals(TenantStatus.FORMER, viewModel.tenants.value[0].status)

        viewModel.deleteTenant("t_test_vm")
        assertTrue(viewModel.tenants.value.isEmpty())
        collectJob.cancel()
    }

    @Test
    fun testViewModelLeaseManagement() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.leases.collect {}
        }

        val lease = Lease(
            id = "l_test_vm",
            propertyId = "p1",
            propertyTitle = "Sunset Heights Apt 4B",
            tenantId = "t1",
            tenantName = "Alice Johnson",
            startDate = "2025-01-01",
            endDate = "2026-01-01",
            monthlyRent = 2400.0,
            securityDeposit = 2400.0,
            status = LeaseStatus.ACTIVE
        )

        viewModel.addLease(lease)
        assertEquals(1, viewModel.leases.value.size)

        val updatedLease = lease.copy(status = LeaseStatus.EXPIRED)
        viewModel.updateLease(updatedLease)
        assertEquals(LeaseStatus.EXPIRED, viewModel.leases.value[0].status)

        viewModel.deleteLease("l_test_vm")
        assertTrue(viewModel.leases.value.isEmpty())
        collectJob.cancel()
    }

    @Test
    fun testViewModelRentPaymentManagement() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.rents.collect {}
        }

        val rent = RentPayment(
            id = "r_test_vm",
            tenantId = "t1",
            tenantName = "Alice Johnson",
            propertyTitle = "Sunset Heights Apt 4B",
            amount = 2400.0,
            dueDate = "2025-04-01",
            paidDate = null,
            status = PaymentStatus.PENDING
        )

        viewModel.addRentPayment(rent)
        assertEquals(1, viewModel.rents.value.size)

        viewModel.markRentPaid("r_test_vm", "2025-04-01")
        assertEquals(PaymentStatus.PAID, viewModel.rents.value[0].status)
        assertEquals("2025-04-01", viewModel.rents.value[0].paidDate)

        viewModel.updateRentStatus("r_test_vm", PaymentStatus.OVERDUE, null)
        assertEquals(PaymentStatus.OVERDUE, viewModel.rents.value[0].status)

        viewModel.deleteRentPayment("r_test_vm")
        assertTrue(viewModel.rents.value.isEmpty())
        collectJob.cancel()
    }

    @Test
    fun testViewModelIsEmptyState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.properties.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.tenants.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.leases.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.rents.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.isEmpty.collect {} }

        assertTrue(viewModel.isEmpty.value)

        viewModel.addProperty(
            Property("p1", "Title", "Address", PropertyType.APARTMENT, 1000.0, 1, 1, PropertyStatus.AVAILABLE)
        )

        assertFalse(viewModel.isEmpty.value)
    }

    @Test
    fun testErrorHandlingAndClear() = runTest {
        assertNull(viewModel.errorMessage.value)

        viewModel.clearError()
        assertNull(viewModel.errorMessage.value)
    }
}

