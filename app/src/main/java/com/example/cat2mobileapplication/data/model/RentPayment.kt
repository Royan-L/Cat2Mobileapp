package com.example.cat2mobileapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RentPayment(
    val id: String,
    val tenantId: String,
    val tenantName: String,
    val propertyTitle: String,
    val amount: Double,
    val dueDate: String,
    val paidDate: String?,
    val status: PaymentStatus
)

@Serializable
enum class PaymentStatus {
    PAID, PENDING, OVERDUE
}
