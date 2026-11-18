package com.example.cat2mobileapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Tenant(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val propertyId: String?,
    val leaseStart: String,
    val leaseEnd: String,
    val status: TenantStatus
)

@Serializable
enum class TenantStatus {
    ACTIVE, PENDING, FORMER
}
