package com.example.cat2mobileapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "properties")
data class Property(
    @PrimaryKey
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
