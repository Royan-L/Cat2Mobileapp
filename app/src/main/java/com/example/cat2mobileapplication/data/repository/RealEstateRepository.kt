package com.example.cat2mobileapplication.data.repository

import com.example.cat2mobileapplication.data.local.dao.LeaseDao
import com.example.cat2mobileapplication.data.local.dao.PropertyDao
import com.example.cat2mobileapplication.data.local.dao.RentPaymentDao
import com.example.cat2mobileapplication.data.local.dao.TenantDao
import com.example.cat2mobileapplication.data.model.Lease
import com.example.cat2mobileapplication.data.model.PaymentStatus
import com.example.cat2mobileapplication.data.model.Property
import com.example.cat2mobileapplication.data.model.RentPayment
import com.example.cat2mobileapplication.data.model.Tenant
import kotlinx.coroutines.flow.Flow

class RealEstateRepository(
    private val propertyDao: PropertyDao,
    private val tenantDao: TenantDao,
    private val rentPaymentDao: RentPaymentDao,
    private val leaseDao: LeaseDao
) {
    val properties: Flow<List<Property>> = propertyDao.getAllProperties()
    val tenants: Flow<List<Tenant>> = tenantDao.getAllTenants()
    val rents: Flow<List<RentPayment>> = rentPaymentDao.getAllRentPayments()
    val leases: Flow<List<Lease>> = leaseDao.getAllLeases()

    suspend fun getPropertyById(id: String): Property? = propertyDao.getPropertyById(id)
    suspend fun addProperty(property: Property) = propertyDao.insertProperty(property)
    suspend fun updateProperty(property: Property) = propertyDao.updateProperty(property)
    suspend fun deleteProperty(propertyId: String) = propertyDao.deletePropertyById(propertyId)

    suspend fun getTenantById(id: String): Tenant? = tenantDao.getTenantById(id)
    suspend fun addTenant(tenant: Tenant) = tenantDao.insertTenant(tenant)
    suspend fun updateTenant(tenant: Tenant) = tenantDao.updateTenant(tenant)
    suspend fun deleteTenant(tenantId: String) = tenantDao.deleteTenantById(tenantId)

    suspend fun getRentPaymentById(id: String): RentPayment? = rentPaymentDao.getRentPaymentById(id)
    suspend fun addRentPayment(rent: RentPayment) = rentPaymentDao.insertRentPayment(rent)
    suspend fun updateRentStatus(paymentId: String, newStatus: PaymentStatus, paidDate: String?) =
        rentPaymentDao.updateRentStatus(paymentId, newStatus, paidDate)
    suspend fun updateRentPayment(rentPayment: RentPayment) = rentPaymentDao.updateRentPayment(rentPayment)
    suspend fun deleteRentPayment(paymentId: String) = rentPaymentDao.deleteRentPaymentById(paymentId)

    suspend fun getLeaseById(id: String): Lease? = leaseDao.getLeaseById(id)
    suspend fun addLease(lease: Lease) = leaseDao.insertLease(lease)
    suspend fun updateLease(lease: Lease) = leaseDao.updateLease(lease)
    suspend fun deleteLease(leaseId: String) = leaseDao.deleteLeaseById(leaseId)
}
