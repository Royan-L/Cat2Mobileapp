package com.example.cat2mobileapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: RealEstateViewModel,
    onNavigateProperties: () -> Unit,
    onNavigateTenants: () -> Unit,
    onNavigateRents: () -> Unit,
    onNavigateLeases: () -> Unit
) {
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()
    val rents by viewModel.rents.collectAsStateWithLifecycle()
    val leases by viewModel.leases.collectAsStateWithLifecycle()

    val totalRentCollected = rents.filter { it.status.name == "PAID" }.sumOf { it.amount }
    val occupancyRate = if (properties.isNotEmpty()) {
        (properties.count { it.status.name == "RENTED" } * 100) / properties.size
    } else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Real Estate Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome Back, Manager",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Here is an overview of your real estate portfolio.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Properties",
                        value = "${properties.size}",
                        icon = Icons.Rounded.Home,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateProperties
                    )
                    StatCard(
                        title = "Occupancy",
                        value = "$occupancyRate%",
                        icon = Icons.Rounded.Business,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateProperties
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Paid Revenue",
                        value = "$${totalRentCollected.toInt()}",
                        icon = Icons.Rounded.AttachMoney,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateRents
                    )
                    StatCard(
                        title = "Active Leases",
                        value = "${leases.size}",
                        icon = Icons.Rounded.Description,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateLeases
                    )
                }
            }

            item {
                Text(
                    text = "Quick Navigation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateProperties,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Properties")
                    }
                    Button(
                        onClick = onNavigateTenants,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.People, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tenants")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateRents,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.ReceiptLong, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rent Collection")
                    }
                    Button(
                        onClick = onNavigateLeases,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Description, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Leases")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
