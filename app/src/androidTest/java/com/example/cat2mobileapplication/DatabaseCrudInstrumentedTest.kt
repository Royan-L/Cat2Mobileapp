package com.example.cat2mobileapplication

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cat2mobileapplication.data.local.AppDatabase
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseCrudInstrumentedTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: RealEstateRepository

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RealEstateRepository(
            propertyDao = database.propertyDao(),
            tenantDao = database.tenantDao(),
            rentPaymentDao = database.rentPaymentDao(),
            leaseDao = database.leaseDao()
        )
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testPropertyCrudOperations() = runBlocking {
        // 1. Create (Insert)
        val testProperty = Property(
            id = "p_test1",
            title = "Test Villa",
            address = "777 Palm Street, Miami, FL",
            type = PropertyType.HOUSE,
            monthlyRent = 5000.0,
            bedrooms = 5,
            bathrooms = 4,
            status = PropertyStatus.AVAILABLE
        )
        repository.addProperty(testProperty)

        // 2. Read (Query)
        val propertiesList = repository.properties.first()
        assertEquals(1, propertiesList.size)
        val retrievedProperty = repository.getPropertyById("p_test1")
        assertNotNull(retrievedProperty)
        assertEquals("Test Villa", retrievedProperty?.title)
        assertEquals(5000.0, retrievedProperty?.monthlyRent ?: 0.0, 0.01)

        // 3. Update
        val updatedProperty = testProperty.copy(
            title = "Updated Luxury Villa",
            status = PropertyStatus.RENTED,
            monthlyRent = 5500.0
        )
        repository.updateProperty(updatedProperty)

        val retrievedAfterUpdate = repository.getPropertyById("p_test1")
        assertNotNull(retrievedAfterUpdate)
        assertEquals("Updated Luxury Villa", retrievedAfterUpdate?.title)
        assertEquals(PropertyStatus.RENTED, retrievedAfterUpdate?.status)
        assertEquals(5500.0, retrievedAfterUpdate?.monthlyRent ?: 0.0, 0.01)

        // 4. Delete
        repository.deleteProperty("p_test1")
        val propertiesAfterDelete = repository.properties.first()
        assertTrue(propertiesAfterDelete.isEmpty())
        assertNull(repository.getPropertyById("p_test1"))
    }

    @Test
    fun testTenantCrudOperations() = runBlocking {
        // 1. Create (Insert)
        val tenant = Tenant(
            id = "t_test1",
            name = "John Doe",
            email = "john.doe@example.com",
            phone = "+1 (555) 999-0000",
            propertyId = "p_test1",
            leaseStart = "2025-01-01",
            leaseEnd = "2026-01-01",
            status = TenantStatus.ACTIVE
        )
        repository.addTenant(tenant)

        // 2. Read
        val tenantsList = repository.tenants.first()
        assertEquals(1, tenantsList.size)
        val fetchedTenant = repository.getTenantById("t_test1")
        assertNotNull(fetchedTenant)
        assertEquals("John Doe", fetchedTenant?.name)
        assertEquals("john.doe@example.com", fetchedTenant?.email)

        // 3. Update
        val updatedTenant = tenant.copy(
            phone = "+1 (555) 111-2222",
            status = TenantStatus.FORMER
        )
        repository.updateTenant(updatedTenant)

        val fetchedUpdatedTenant = repository.getTenantById("t_test1")
        assertNotNull(fetchedUpdatedTenant)
        assertEquals("+1 (555) 111-2222", fetchedUpdatedTenant?.phone)
        assertEquals(TenantStatus.FORMER, fetchedUpdatedTenant?.status)

        // 4. Delete
        repository.deleteTenant("t_test1")
        val tenantsAfterDelete = repository.tenants.first()
        assertTrue(tenantsAfterDelete.isEmpty())
        assertNull(repository.getTenantById("t_test1"))
    }

    @Test
    fun testLeaseCrudOperations() = runBlocking {
        // 1. Create
        val lease = Lease(
            id = "l_test1",
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
        repository.addLease(lease)

        // 2. Read
        val leasesList = repository.leases.first()
        assertEquals(1, leasesList.size)
        val fetchedLease = repository.getLeaseById("l_test1")
        assertNotNull(fetchedLease)
        assertEquals("Sunset Heights Apt 4B", fetchedLease?.propertyTitle)

        // 3. Update
        val updatedLease = lease.copy(
            monthlyRent = 2600.0,
            status = LeaseStatus.EXPIRED
        )
        repository.updateLease(updatedLease)

        val fetchedUpdatedLease = repository.getLeaseById("l_test1")
        assertNotNull(fetchedUpdatedLease)
        assertEquals(2600.0, fetchedUpdatedLease?.monthlyRent ?: 0.0, 0.01)
        assertEquals(LeaseStatus.EXPIRED, fetchedUpdatedLease?.status)

        // 4. Delete
        repository.deleteLease("l_test1")
        val leasesAfterDelete = repository.leases.first()
        assertTrue(leasesAfterDelete.isEmpty())
    }

    @Test
    fun testRentPaymentCrudOperations() = runBlocking {
        // 1. Create
        val rentPayment = RentPayment(
            id = "r_test1",
            tenantId = "t1",
            tenantName = "Alice Johnson",
            propertyTitle = "Sunset Heights Apt 4B",
            amount = 2400.0,
            dueDate = "2025-03-01",
            paidDate = null,
            status = PaymentStatus.PENDING
        )
        repository.addRentPayment(rentPayment)

        // 2. Read
        val rentsList = repository.rents.first()
        assertEquals(1, rentsList.size)
        val fetchedRent = repository.getRentPaymentById("r_test1")
        assertNotNull(fetchedRent)
        assertEquals(PaymentStatus.PENDING, fetchedRent?.status)

        // 3. Update status (e.g., mark paid)
        repository.updateRentStatus("r_test1", PaymentStatus.PAID, "2025-03-01")

        val fetchedPaidRent = repository.getRentPaymentById("r_test1")
        assertNotNull(fetchedPaidRent)
        assertEquals(PaymentStatus.PAID, fetchedPaidRent?.status)
        assertEquals("2025-03-01", fetchedPaidRent?.paidDate)

        // 4. Delete
        repository.deleteRentPayment("r_test1")
        val rentsAfterDelete = repository.rents.first()
        assertTrue(rentsAfterDelete.isEmpty())
    }
}
