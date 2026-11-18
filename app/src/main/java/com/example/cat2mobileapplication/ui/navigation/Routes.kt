package com.example.cat2mobileapplication.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute : NavKey

@Serializable
data object PropertiesRoute : NavKey

@Serializable
data object TenantsRoute : NavKey

@Serializable
data object RentCollectionRoute : NavKey

@Serializable
data object LeaseManagementRoute : NavKey

@Serializable
data class PropertyDetailRoute(val propertyId: String) : NavKey

@Serializable
data class TenantDetailRoute(val tenantId: String) : NavKey
