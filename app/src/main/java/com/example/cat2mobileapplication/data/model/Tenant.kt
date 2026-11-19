package com.example.cat2mobileapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tenants")
data class Tenant(
    @PrimaryKey
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
