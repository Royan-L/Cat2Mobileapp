package com.example.cat2mobileapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "leases")
data class Lease(
    @PrimaryKey
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val tenantId: String,
    val tenantName: String,
    val startDate: String,
    val endDate: String,
    val monthlyRent: Double,
    val securityDeposit: Double,
    val status: LeaseStatus
)

@Serializable
enum class LeaseStatus {
    ACTIVE, EXPIRED, PENDING_APPROVAL, TERMINATED
}
