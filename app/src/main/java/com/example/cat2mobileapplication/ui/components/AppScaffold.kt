package com.example.cat2mobileapplication.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.cat2mobileapplication.ui.navigation.*
import com.example.cat2mobileapplication.ui.screens.*
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel

@Composable
fun AppScaffold(viewModel: RealEstateViewModel = viewModel()) {
    val navigationState = rememberNavigationState(
        startRoute = DashboardRoute,
        topLevelRoutes = setOf(
            DashboardRoute,
            PropertiesRoute,
            TenantsRoute,
            RentCollectionRoute,
            LeaseManagementRoute
        )
    )
    val navigator = remember { Navigator(navigationState) }

    val entryProvider = entryProvider {
        entry<DashboardRoute> {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateProperties = { navigator.navigate(PropertiesRoute) },
                onNavigateTenants = { navigator.navigate(TenantsRoute) },
                onNavigateRents = { navigator.navigate(RentCollectionRoute) },
                onNavigateLeases = { navigator.navigate(LeaseManagementRoute) }
            )
        }
        entry<PropertiesRoute> {
            PropertiesScreen(
                viewModel = viewModel,
                onPropertyClick = { propertyId ->
                    navigator.navigate(PropertyDetailRoute(propertyId))
                }
            )
        }
        entry<TenantsRoute> {
            TenantsScreen(
                viewModel = viewModel,
                onTenantClick = { tenantId ->
                    navigator.navigate(TenantDetailRoute(tenantId))
                }
            )
        }
        entry<RentCollectionRoute> {
            RentCollectionScreen(viewModel = viewModel)
        }
        entry<LeaseManagementRoute> {
            LeaseManagementScreen(viewModel = viewModel)
        }
        entry<PropertyDetailRoute> { key ->
            PropertyDetailScreen(
                propertyId = key.propertyId,
                viewModel = viewModel,
                onBack = { navigator.goBack() }
            )
        }
        entry<TenantDetailRoute> { key ->
            TenantDetailScreen(
                tenantId = key.tenantId,
                viewModel = viewModel,
                onBack = { navigator.goBack() },
                onPropertyClick = { propertyId ->
                    navigator.navigate(PropertyDetailRoute(propertyId))
                }
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (navigationState.topLevelRoute in setOf(DashboardRoute, PropertiesRoute, TenantsRoute, RentCollectionRoute, LeaseManagementRoute)) {
                NavigationBar {
                    NavigationBarItem(
                        selected = navigationState.topLevelRoute == DashboardRoute,
                        onClick = { navigator.navigate(DashboardRoute) },
                        icon = { Icon(Icons.Rounded.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") }
                    )
                    NavigationBarItem(
                        selected = navigationState.topLevelRoute == PropertiesRoute,
                        onClick = { navigator.navigate(PropertiesRoute) },
                        icon = { Icon(Icons.Rounded.Home, contentDescription = "Properties") },
                        label = { Text("Properties") }
                    )
                    NavigationBarItem(
                        selected = navigationState.topLevelRoute == TenantsRoute,
                        onClick = { navigator.navigate(TenantsRoute) },
                        icon = { Icon(Icons.Rounded.People, contentDescription = "Tenants") },
                        label = { Text("Tenants") }
                    )
                    NavigationBarItem(
                        selected = navigationState.topLevelRoute == RentCollectionRoute,
                        onClick = { navigator.navigate(RentCollectionRoute) },
                        icon = { Icon(Icons.Rounded.Receipt, contentDescription = "Rent") },
                        label = { Text("Rent") }
                    )
                    NavigationBarItem(
                        selected = navigationState.topLevelRoute == LeaseManagementRoute,
                        onClick = { navigator.navigate(LeaseManagementRoute) },
                        icon = { Icon(Icons.Rounded.Description, contentDescription = "Leases") },
                        label = { Text("Leases") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
