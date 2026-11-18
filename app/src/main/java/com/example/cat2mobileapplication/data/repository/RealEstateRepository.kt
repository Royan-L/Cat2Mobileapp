package com.example.cat2mobileapplication.data.repository

import com.example.cat2mobileapplication.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RealEstateRepository {

    private val _properties = MutableStateFlow(
        listOf(
            Property(
                id = "p1",
                title = "Sunset Heights Apt 4B",
                address = "123 Ocean Drive, Miami, FL",
                type = PropertyType.APARTMENT,
                monthlyRent = 2400.0,
                bedrooms = 2,
                bathrooms = 2,
                status = PropertyStatus.RENTED
            ),
            Property(
                id = "p2",
                title = "Maplewood Suburban House",
                address = "456 Oak Avenue, Austin, TX",
                type = PropertyType.HOUSE,
                monthlyRent = 3200.0,
                bedrooms = 4,
                bathrooms = 3,
                status = PropertyStatus.RENTED
            ),
            Property(
                id = "p3",
                title = "Downtown Luxury Condo",
                address = "789 Broadway, New York, NY",
                type = PropertyType.CONDO,
                monthlyRent = 4500.0,
                bedrooms = 1,
                bathrooms = 1.5.toInt(),
                status = PropertyStatus.AVAILABLE
            ),
            Property(
                id = "p4",
                title = "Silicon Valley Studio",
                address = "101 Tech Way, San Jose, CA",
                type = PropertyType.STUDIO,
                monthlyRent = 2100.0,
                bedrooms = 1,
                bathrooms = 1,
                status = PropertyStatus.AVAILABLE
            )
        )
    )
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _tenants = MutableStateFlow(
        listOf(
            Tenant(
                id = "t1",
                name = "Alice Johnson",
                email = "alice.j@example.com",
                phone = "+1 (555) 019-2834",
                propertyId = "p1",
                leaseStart = "2024-01-01",
                leaseEnd = "2025-01-01",
                status = TenantStatus.ACTIVE
            ),
            Tenant(
                id = "t2",
                name = "Bob Smith",
                email = "bob.smith@example.com",
                phone = "+1 (555) 048-5920",
                propertyId = "p2",
                leaseStart = "2023-06-15",
                leaseEnd = "2025-06-15",
                status = TenantStatus.ACTIVE
            ),
            Tenant(
                id = "t3",
                name = "Charlie Davis",
                email = "charlie.d@example.com",
                phone = "+1 (555) 839-2011",
                propertyId = null,
                leaseStart = "",
                leaseEnd = "",
                status = TenantStatus.PENDING
            )
        )
    )
    val tenants: StateFlow<List<Tenant>> = _tenants.asStateFlow()

    private val _rents = MutableStateFlow(
        listOf(
            RentPayment(
                id = "r1",
                tenantId = "t1",
                tenantName = "Alice Johnson",
                propertyTitle = "Sunset Heights Apt 4B",
                amount = 2400.0,
                dueDate = "2025-02-01",
                paidDate = "2025-02-01",
                status = PaymentStatus.PAID
            ),
            RentPayment(
                id = "r2",
                tenantId = "t2",
                tenantName = "Bob Smith",
                propertyTitle = "Maplewood Suburban House",
                amount = 3200.0,
                dueDate = "2025-02-01",
                paidDate = null,
                status = PaymentStatus.OVERDUE
            ),
            RentPayment(
                id = "r3",
                tenantId = "t1",
                tenantName = "Alice Johnson",
                propertyTitle = "Sunset Heights Apt 4B",
                amount = 2400.0,
                dueDate = "2025-03-01",
                paidDate = null,
                status = PaymentStatus.PENDING
            )
        )
    )
    val rents: StateFlow<List<RentPayment>> = _rents.asStateFlow()

    private val _leases = MutableStateFlow(
        listOf(
            Lease(
                id = "l1",
                propertyId = "p1",
                propertyTitle = "Sunset Heights Apt 4B",
                tenantId = "t1",
                tenantName = "Alice Johnson",
                startDate = "2024-01-01",
                endDate = "2025-01-01",
                monthlyRent = 2400.0,
                securityDeposit = 2400.0,
                status = LeaseStatus.ACTIVE
            ),
            Lease(
                id = "l2",
                propertyId = "p2",
                propertyTitle = "Maplewood Suburban House",
                tenantId = "t2",
                tenantName = "Bob Smith",
                startDate = "2023-06-15",
                endDate = "2025-06-15",
                monthlyRent = 3200.0,
                securityDeposit = 3200.0,
                status = LeaseStatus.ACTIVE
            )
        )
    )
    val leases: StateFlow<List<Lease>> = _leases.asStateFlow()

    fun addRentPayment(rent: RentPayment) {
        _rents.value = _rents.value + rent
    }

    fun updateRentStatus(paymentId: String, newStatus: PaymentStatus, paidDate: String?) {
        _rents.value = _rents.value.map { rent ->
            if (rent.id == paymentId) {
                rent.copy(status = newStatus, paidDate = paidDate)
            } else {
                rent
            }
        }
    }

    fun deleteRentPayment(paymentId: String) {
        _rents.value = _rents.value.filter { it.id != paymentId }
    }

    fun addProperty(property: Property) {
        _properties.value = _properties.value + property
    }

    fun updateProperty(updatedProperty: Property) {
        _properties.value = _properties.value.map { if (it.id == updatedProperty.id) updatedProperty else it }
    }

    fun deleteProperty(propertyId: String) {
        _properties.value = _properties.value.filter { it.id != propertyId }
    }

    fun addTenant(tenant: Tenant) {
        _tenants.value = _tenants.value + tenant
    }

    fun updateTenant(updatedTenant: Tenant) {
        _tenants.value = _tenants.value.map { if (it.id == updatedTenant.id) updatedTenant else it }
    }

    fun deleteTenant(tenantId: String) {
        _tenants.value = _tenants.value.filter { it.id != tenantId }
    }

    fun addLease(lease: Lease) {
        _leases.value = _leases.value + lease
    }

    fun updateLease(updatedLease: Lease) {
        _leases.value = _leases.value.map { if (it.id == updatedLease.id) updatedLease else it }
    }

    fun deleteLease(leaseId: String) {
        _leases.value = _leases.value.filter { it.id != leaseId }
    }
}
