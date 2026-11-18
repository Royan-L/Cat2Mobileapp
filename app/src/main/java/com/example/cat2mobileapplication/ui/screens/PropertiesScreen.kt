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
import com.example.cat2mobileapplication.data.model.Property
import com.example.cat2mobileapplication.data.model.PropertyStatus
import com.example.cat2mobileapplication.data.model.PropertyType
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    viewModel: RealEstateViewModel,
    onPropertyClick: (String) -> Unit
) {
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<PropertyStatus?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredProperties = properties.filter { property ->
        val matchesSearch = searchQuery.isBlank() ||
                property.title.contains(searchQuery, ignoreCase = true) ||
                property.address.contains(searchQuery, ignoreCase = true) ||
                property.type.name.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == null || property.status == selectedFilter
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Properties Directory", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add Property")
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
                Icon(Icons.Rounded.Add, contentDescription = "Add Property")
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
                placeholder = { Text("Search by title, address, or type...") },
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
                    selected = selectedFilter == PropertyStatus.AVAILABLE,
                    onClick = { selectedFilter = PropertyStatus.AVAILABLE },
                    label = { Text("Available") }
                )
                FilterChip(
                    selected = selectedFilter == PropertyStatus.RENTED,
                    onClick = { selectedFilter = PropertyStatus.RENTED },
                    label = { Text("Rented") }
                )
                FilterChip(
                    selected = selectedFilter == PropertyStatus.MAINTENANCE,
                    onClick = { selectedFilter = PropertyStatus.MAINTENANCE },
                    label = { Text("Maintenance") }
                )
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                filteredProperties.isEmpty() -> {
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
                                imageVector = Icons.Rounded.HomeWork,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "No properties found",
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
                                    Text("Add Property")
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
                        items(filteredProperties) { property ->
                            PropertyCard(property = property, onClick = { onPropertyClick(property.id) })
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPropertyDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newProperty ->
                viewModel.addProperty(newProperty)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PropertyCard(property: Property, onClick: () -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Badge(
                    containerColor = when (property.status) {
                        PropertyStatus.AVAILABLE -> MaterialTheme.colorScheme.tertiaryContainer
                        PropertyStatus.RENTED -> MaterialTheme.colorScheme.primaryContainer
                        PropertyStatus.MAINTENANCE -> MaterialTheme.colorScheme.errorContainer
                        PropertyStatus.SOLD -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(
                        text = property.status.name,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Text(
                text = property.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${property.type} • ${property.bedrooms} Bed • ${property.bathrooms} Bath",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "$${property.monthlyRent.toInt()} /mo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AddPropertyDialog(
    onDismiss: () -> Unit,
    onAdd: (Property) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PropertyType.APARTMENT) }
    var monthlyRent by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(PropertyStatus.AVAILABLE) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Property") },
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
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Property Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = monthlyRent,
                    onValueChange = { monthlyRent = it },
                    label = { Text("Monthly Rent ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bedrooms,
                        onValueChange = { bedrooms = it },
                        label = { Text("Bedrooms") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bathrooms,
                        onValueChange = { bathrooms = it },
                        label = { Text("Bathrooms") },
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
                    if (title.isBlank() || address.isBlank() || monthlyRent.isBlank() || bedrooms.isBlank() || bathrooms.isBlank()) {
                        errorText = "Please fill in all fields"
                        return@Button
                    }
                    val rentVal = monthlyRent.toDoubleOrNull()
                    val bedVal = bedrooms.toIntOrNull()
                    val bathVal = bathrooms.toIntOrNull()
                    if (rentVal == null || bedVal == null || bathVal == null) {
                        errorText = "Invalid number format"
                        return@Button
                    }
                    val property = Property(
                        id = "p_${UUID.randomUUID().toString().take(6)}",
                        title = title.trim(),
                        address = address.trim(),
                        type = type,
                        monthlyRent = rentVal,
                        bedrooms = bedVal,
                        bathrooms = bathVal,
                        status = status
                    )
                    onAdd(property)
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
