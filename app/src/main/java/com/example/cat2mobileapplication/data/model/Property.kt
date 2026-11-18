package com.example.cat2mobileapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val id: String,
    val title: String,
    val address: String,
    val type: PropertyType,
    val monthlyRent: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val status: PropertyStatus,
    val imageUrl: String = ""
)

@Serializable
enum class PropertyType {
    APARTMENT, HOUSE, CONDO, COMMERCIAL, STUDIO
}

@Serializable
enum class PropertyStatus {
    AVAILABLE, RENTED, MAINTENANCE, SOLD
}
