package com.example.cat2mobileapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cat2mobileapplication.data.model.TenantStatus
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantDetailScreen(
    tenantId: String,
    viewModel: RealEstateViewModel,
    onBack: () -> Unit,
    onPropertyClick: (String) -> Unit
) {
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val actualTenant = tenants.find { it.id == tenantId }
    val assignedProperty = properties.find { it.id == actualTenant?.propertyId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(actualTenant?.name ?: "Tenant Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (actualTenant != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit Tenant")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete Tenant")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        if (actualTenant == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Tenant not found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = actualTenant.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Badge(
                                containerColor = when (actualTenant.status) {
                                    TenantStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                                    TenantStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                                    TenantStatus.FORMER -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            ) {
                                Text(
                                    text = actualTenant.status.name,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        Text(
                            text = "Email: ${actualTenant.email}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Phone: ${actualTenant.phone}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider()

                        if (actualTenant.leaseStart.isNotEmpty() && actualTenant.leaseEnd.isNotEmpty()) {
                            Text(
                                text = "Lease Period: ${actualTenant.leaseStart} to ${actualTenant.leaseEnd}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Card(
                    onClick = {
                        if (assignedProperty != null) {
                            onPropertyClick(assignedProperty.id)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Assigned Property",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (assignedProperty != null) {
                            Text(
                                text = assignedProperty.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = assignedProperty.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Monthly Rent: $${assignedProperty.monthlyRent.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        } else {
                            Text(
                                text = "No property currently assigned.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.updateTenant(actualTenant.copy(status = TenantStatus.ACTIVE))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Set Active")
                    }
                    Button(
                        onClick = {
                            viewModel.updateTenant(actualTenant.copy(status = TenantStatus.FORMER))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("Set Former")
                    }
                }
            }
        }
    }

    if (showDeleteConfirm && actualTenant != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Tenant") },
            text = { Text("Are you sure you want to delete ${actualTenant.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTenant(actualTenant.id)
                        showDeleteConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
