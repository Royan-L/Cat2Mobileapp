package com.example.cat2mobileapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Lease(
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
