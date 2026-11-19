package com.example.cat2mobileapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "rent_payments")
data class RentPayment(
    @PrimaryKey
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
