package com.example.cat2mobileapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cat2mobileapplication.data.model.Lease
import com.example.cat2mobileapplication.data.model.LeaseStatus
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseManagementScreen(viewModel: RealEstateViewModel) {
    val leases by viewModel.leases.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<LeaseStatus?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val filteredLeases = leases.filter { lease ->
        val matchesSearch = searchQuery.isBlank() ||
                lease.tenantName.contains(searchQuery, ignoreCase = true) ||
                lease.propertyTitle.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == null || lease.status == selectedFilter
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lease Agreement Management", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add Lease")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Rounded.Description, contentDescription = "Add Lease")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = error, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            feedbackMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = msg, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.weight(1f))
                        TextButton(onClick = { feedbackMessage = null }) {
                            Text("OK")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by tenant or property...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == LeaseStatus.ACTIVE,
                    onClick = { selectedFilter = LeaseStatus.ACTIVE },
                    label = { Text("Active") }
                )
                FilterChip(
                    selected = selectedFilter == LeaseStatus.EXPIRED,
                    onClick = { selectedFilter = LeaseStatus.EXPIRED },
                    label = { Text("Expired") }
                )
                FilterChip(
                    selected = selectedFilter == LeaseStatus.PENDING_APPROVAL,
                    onClick = { selectedFilter = LeaseStatus.PENDING_APPROVAL },
                    label = { Text("Pending") }
                )
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                filteredLeases.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Description,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "No lease agreements found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (searchQuery.isNotEmpty() || selectedFilter != null) {
                                Button(onClick = {
                                    searchQuery = ""
                                    selectedFilter = null
                                }) {
                                    Text("Reset Filters")
                                }
                            } else {
                                Button(onClick = { showAddDialog = true }) {
                                    Text("Add Lease")
                                }
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredLeases) { lease ->
                            LeaseCard(
                                lease = lease,
                                onUpdateStatus = { newStatus ->
                                    val updated = lease.copy(status = newStatus)
                                    viewModel.updateLease(updated)
                                    feedbackMessage = "Updated lease status to $newStatus"
                                },
                                onDelete = {
                                    viewModel.deleteLease(lease.id)
                                    feedbackMessage = "Deleted lease agreement"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddLeaseDialog(
            properties = properties,
            tenants = tenants,
            onDismiss = { showAddDialog = false },
            onAdd = { newLease ->
                viewModel.addLease(newLease)
                showAddDialog = false
                feedbackMessage = "Created lease agreement for ${newLease.tenantName}"
            }
        )
    }
}

@Composable
fun LeaseCard(
    lease: Lease,
    onUpdateStatus: (LeaseStatus) -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lease.propertyTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(
                        containerColor = when (lease.status) {
                            LeaseStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                            LeaseStatus.EXPIRED -> MaterialTheme.colorScheme.errorContainer
                            LeaseStatus.PENDING_APPROVAL -> MaterialTheme.colorScheme.tertiaryContainer
                            LeaseStatus.TERMINATED -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    ) {
                        Text(
                            text = lease.status.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Set Active") },
                                onClick = {
                                    showMenu = false
                                    onUpdateStatus(LeaseStatus.ACTIVE)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Set Expired") },
                                onClick = {
                                    showMenu = false
                                    onUpdateStatus(LeaseStatus.EXPIRED)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Terminate") },
                                onClick = {
                                    showMenu = false
                                    onUpdateStatus(LeaseStatus.TERMINATED)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
            Text(
                text = "Tenant: ${lease.tenantName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Term: ${lease.startDate} to ${lease.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Security Deposit: $${lease.securityDeposit.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = "$${lease.monthlyRent.toInt()} /mo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLeaseDialog(
    properties: List<com.example.cat2mobileapplication.data.model.Property>,
    tenants: List<com.example.cat2mobileapplication.data.model.Tenant>,
    onDismiss: () -> Unit,
    onAdd: (Lease) -> Unit
) {
    var selectedProperty by remember { mutableStateOf(properties.firstOrNull()) }
    var selectedTenant by remember { mutableStateOf(tenants.firstOrNull()) }
    var startDate by remember { mutableStateOf("2025-04-01") }
    var endDate by remember { mutableStateOf("2026-04-01") }
    var monthlyRent by remember { mutableStateOf(selectedProperty?.monthlyRent?.toString() ?: "2500") }
    var securityDeposit by remember { mutableStateOf("2500") }
    var status by remember { mutableStateOf(LeaseStatus.ACTIVE) }
    var errorText by remember { mutableStateOf<String?>(null) }

    var propertyExpanded by remember { mutableStateOf(false) }
    var tenantExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Lease Agreement") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                errorText?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Property Selector
                ExposedDropdownMenuBox(
                    expanded = propertyExpanded,
                    onExpandedChange = { propertyExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedProperty?.title ?: "Select Property",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Property") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = propertyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = propertyExpanded,
                        onDismissRequest = { propertyExpanded = false }
                    ) {
                        properties.forEach { property ->
                            DropdownMenuItem(
                                text = { Text(property.title) },
                                onClick = {
                                    selectedProperty = property
                                    monthlyRent = property.monthlyRent.toInt().toString()
                                    securityDeposit = property.monthlyRent.toInt().toString()
                                    propertyExpanded = false
                                }
                            )
                        }
                    }
                }

                // Tenant Selector
                ExposedDropdownMenuBox(
                    expanded = tenantExpanded,
                    onExpandedChange = { tenantExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTenant?.name ?: "Select Tenant",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tenant") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tenantExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = tenantExpanded,
                        onDismissRequest = { tenantExpanded = false }
                    ) {
                        tenants.forEach { tenant ->
                            DropdownMenuItem(
                                text = { Text(tenant.name) },
                                onClick = {
                                    selectedTenant = tenant
                                    tenantExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Start Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("End Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = monthlyRent,
                        onValueChange = { monthlyRent = it },
                        label = { Text("Monthly Rent ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = securityDeposit,
                        onValueChange = { securityDeposit = it },
                        label = { Text("Security Deposit ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedProperty == null || selectedTenant == null || startDate.isBlank() || endDate.isBlank() || monthlyRent.isBlank() || securityDeposit.isBlank()) {
                        errorText = "Please fill in all fields"
                        return@Button
                    }
                    val rentVal = monthlyRent.toDoubleOrNull()
                    val depositVal = securityDeposit.toDoubleOrNull()
                    if (rentVal == null || depositVal == null) {
                        errorText = "Invalid numerical format for rent or deposit"
                        return@Button
                    }
                    val lease = Lease(
                        id = "l_${UUID.randomUUID().toString().take(6)}",
                        propertyId = selectedProperty!!.id,
                        propertyTitle = selectedProperty!!.title,
                        tenantId = selectedTenant!!.id,
                        tenantName = selectedTenant!!.name,
                        startDate = startDate.trim(),
                        endDate = endDate.trim(),
                        monthlyRent = rentVal,
                        securityDeposit = depositVal,
                        status = status
                    )
                    onAdd(lease)
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
