package com.example.cat2mobileapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cat2mobileapplication.RealEstateApplication
import com.example.cat2mobileapplication.data.model.Lease
import com.example.cat2mobileapplication.data.model.PaymentStatus
import com.example.cat2mobileapplication.data.model.Property
import com.example.cat2mobileapplication.data.model.RentPayment
import com.example.cat2mobileapplication.data.model.Tenant
import com.example.cat2mobileapplication.data.repository.RealEstateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RealEstateViewModel(
    private val repository: RealEstateRepository
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
            try {
                _isLoading.value = true
                repository.addProperty(property)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to add property"
            }
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateProperty(property)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to update property"
            }
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteProperty(propertyId)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to delete property"
            }
        }
    }

    fun addTenant(tenant: Tenant) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addTenant(tenant)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to add tenant"
            }
        }
    }

    fun updateTenant(tenant: Tenant) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateTenant(tenant)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to update tenant"
            }
        }
    }

    fun deleteTenant(tenantId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteTenant(tenantId)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to delete tenant"
            }
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RealEstateApplication)
                RealEstateViewModel(application.repository)
            }
        }
    }
}
