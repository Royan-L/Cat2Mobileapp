package com.example.cat2mobileapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cat2mobileapplication.data.model.Tenant
import com.example.cat2mobileapplication.data.model.TenantStatus
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel
import com.example.cat2mobileapplication.ui.validation.InputValidator
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantsScreen(
    viewModel: RealEstateViewModel,
    onTenantClick: (String) -> Unit
) {
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<TenantStatus?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredTenants = tenants.filter { tenant ->
        val matchesSearch = searchQuery.isBlank() ||
                tenant.name.contains(searchQuery, ignoreCase = true) ||
                tenant.email.contains(searchQuery, ignoreCase = true) ||
                tenant.phone.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == null || tenant.status == selectedFilter
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tenant Directory", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add Tenant")
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
                Icon(Icons.Rounded.PersonAdd, contentDescription = "Add Tenant")
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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by name, email, or phone...") },
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
                    selected = selectedFilter == TenantStatus.ACTIVE,
                    onClick = { selectedFilter = TenantStatus.ACTIVE },
                    label = { Text("Active") }
                )
                FilterChip(
                    selected = selectedFilter == TenantStatus.PENDING,
                    onClick = { selectedFilter = TenantStatus.PENDING },
                    label = { Text("Pending") }
                )
                FilterChip(
                    selected = selectedFilter == TenantStatus.FORMER,
                    onClick = { selectedFilter = TenantStatus.FORMER },
                    label = { Text("Former") }
                )
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                filteredTenants.isEmpty() -> {
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
                                imageVector = Icons.Rounded.PeopleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "No tenants found",
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
                                    Text("Add Tenant")
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
                        items(filteredTenants) { tenant ->
                            val assignedProperty = properties.find { it.id == tenant.propertyId }
                            TenantCard(
                                tenant = tenant,
                                propertyTitle = assignedProperty?.title,
                                onClick = { onTenantClick(tenant.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTenantDialog(
            properties = properties,
            onDismiss = { showAddDialog = false },
            onAdd = { newTenant ->
                viewModel.addTenant(newTenant)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TenantCard(tenant: Tenant, propertyTitle: String?, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tenant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Badge(
                    containerColor = when (tenant.status) {
                        TenantStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                        TenantStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                        TenantStatus.FORMER -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(
                        text = tenant.status.name,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Text(
                text = "Email: ${tenant.email}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Phone: ${tenant.phone}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (propertyTitle != null) {
                Text(
                    text = "Property: $propertyTitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            if (tenant.leaseStart.isNotEmpty() && tenant.leaseEnd.isNotEmpty()) {
                Text(
                    text = "Lease: ${tenant.leaseStart} to ${tenant.leaseEnd}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun AddTenantDialog(
    properties: List<com.example.cat2mobileapplication.data.model.Property>,
    onDismiss: () -> Unit,
    onAdd: (Tenant) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var leaseStart by remember { mutableStateOf("2025-01-01") }
    var leaseEnd by remember { mutableStateOf("2026-01-01") }
    var status by remember { mutableStateOf(TenantStatus.ACTIVE) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Tenant") },
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tenant Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = leaseStart,
                    onValueChange = { leaseStart = it },
                    label = { Text("Lease Start (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = leaseEnd,
                    onValueChange = { leaseEnd = it },
                    label = { Text("Lease End (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        !InputValidator.isValidName(name) -> {
                            errorText = "Name must contain at least 2 characters"
                        }
                        !InputValidator.isValidEmail(email) -> {
                            errorText = "Please enter a valid email address"
                        }
                        !InputValidator.isValidPhone(phone) -> {
                            errorText = "Please enter a valid phone number"
                        }
                        !InputValidator.isValidDateRange(leaseStart, leaseEnd) -> {
                            errorText = "Please enter valid lease dates. End date cannot be before start date."
                        }
                        else -> {
                            val tenant = Tenant(
                                id = "t_${UUID.randomUUID().toString().take(6)}",
                                name = name.trim(),
                                email = email.trim(),
                                phone = phone.trim(),
                                propertyId = properties.firstOrNull()?.id,
                                leaseStart = leaseStart.trim(),
                                leaseEnd = leaseEnd.trim(),
                                status = status
                            )
                            onAdd(tenant)
                        }
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}