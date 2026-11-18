package com.example.cat2mobileapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat2mobileapplication.data.model.*
import com.example.cat2mobileapplication.data.repository.RealEstateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RealEstateViewModel(
    private val repository: RealEstateRepository = RealEstateRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val properties: StateFlow<List<Property>> = repository.properties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenants: StateFlow<List<Tenant>> = repository.tenants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rents: StateFlow<List<RentPayment>> = repository.rents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leases: StateFlow<List<Lease>> = repository.leases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isEmpty: StateFlow<Boolean> = combine(properties, tenants, rents, leases) { p, t, r, l ->
        p.isEmpty() && t.isEmpty() && r.isEmpty() && l.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun addRentPayment(rent: RentPayment) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addRentPayment(rent)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to add rent payment"
            }
        }
    }

    fun markRentPaid(paymentId: String, paidDate: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateRentStatus(paymentId, PaymentStatus.PAID, paidDate)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to update payment status"
            }
        }
    }

    fun updateRentStatus(paymentId: String, newStatus: PaymentStatus, paidDate: String?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateRentStatus(paymentId, newStatus, paidDate)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to update payment status"
            }
        }
    }

    fun deleteRentPayment(paymentId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteRentPayment(paymentId)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to delete payment"
            }
        }
    }

    fun addProperty(property: Property) {
        viewModelScope.launch {
            repository.addProperty(property)
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch {
            repository.updateProperty(property)
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            repository.deleteProperty(propertyId)
        }
    }

    fun addTenant(tenant: Tenant) {
        viewModelScope.launch {
            repository.addTenant(tenant)
        }
    }

    fun updateTenant(tenant: Tenant) {
        viewModelScope.launch {
            repository.updateTenant(tenant)
        }
    }

    fun deleteTenant(tenantId: String) {
        viewModelScope.launch {
            repository.deleteTenant(tenantId)
        }
    }

    fun addLease(lease: Lease) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addLease(lease)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to add lease"
            }
        }
    }

    fun updateLease(lease: Lease) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateLease(lease)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to update lease"
            }
        }
    }

    fun deleteLease(leaseId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteLease(leaseId)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to delete lease"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
