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
import com.example.cat2mobileapplication.data.model.PaymentStatus
import com.example.cat2mobileapplication.data.model.RentPayment
import com.example.cat2mobileapplication.ui.viewmodel.RealEstateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentCollectionScreen(viewModel: RealEstateViewModel) {
    val rents by viewModel.rents.collectAsStateWithLifecycle()
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<PaymentStatus?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val totalCollected = rents.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
    val totalPending = rents.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount }
    val totalOverdue = rents.filter { it.status == PaymentStatus.OVERDUE }.sumOf { it.amount }

    val filteredRents = rents.filter { rent ->
        val matchesSearch = searchQuery.isBlank() ||
                rent.tenantName.contains(searchQuery, ignoreCase = true) ||
                rent.propertyTitle.contains(searchQuery, ignoreCase = true) ||
                rent.amount.toString().contains(searchQuery)
        val matchesFilter = selectedFilter == null || rent.status == selectedFilter
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rent Collection & Tracking", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Record Payment")
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
                Icon(Icons.Rounded.Receipt, contentDescription = "Record Payment")
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

            // Financial Overview Metrics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricItem(
                        label = "Collected",
                        amount = totalCollected,
                        color = MaterialTheme.colorScheme.primary
                    )
                    MetricItem(
                        label = "Pending",
                        amount = totalPending,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    MetricItem(
                        label = "Overdue",
                        amount = totalOverdue,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by tenant, property, or amount...") },
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
                    selected = selectedFilter == PaymentStatus.PAID,
                    onClick = { selectedFilter = PaymentStatus.PAID },
                    label = { Text("Paid") }
                )
                FilterChip(
                    selected = selectedFilter == PaymentStatus.PENDING,
                    onClick = { selectedFilter = PaymentStatus.PENDING },
                    label = { Text("Pending") }
                )
                FilterChip(
                    selected = selectedFilter == PaymentStatus.OVERDUE,
                    onClick = { selectedFilter = PaymentStatus.OVERDUE },
                    label = { Text("Overdue") }
                )
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                filteredRents.isEmpty() -> {
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
                                imageVector = Icons.Rounded.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "No rent payments found",
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
                                    Text("Record Payment")
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
                        items(filteredRents) { rent ->
                            RentCard(
                                rent = rent,
                                onMarkPaid = {
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val today = dateFormat.format(Date())
                                    viewModel.markRentPaid(rent.id, today)
                                    feedbackMessage = "Marked payment for ${rent.tenantName} as Paid"
                                },
                                onUpdateStatus = { newStatus ->
                                    val paidDate = if (newStatus == PaymentStatus.PAID) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) else null
                                    viewModel.updateRentStatus(rent.id, newStatus, paidDate)
                                    feedbackMessage = "Updated status to $newStatus"
                                },
                                onDelete = {
                                    viewModel.deleteRentPayment(rent.id)
                                    feedbackMessage = "Deleted rent record"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddRentPaymentDialog(
            tenants = tenants,
            properties = properties,
            onDismiss = { showAddDialog = false },
            onAdd = { newPayment ->
                viewModel.addRentPayment(newPayment)
                showAddDialog = false
                feedbackMessage = "Successfully recorded payment for ${newPayment.tenantName}"
            }
        )
    }
}

@Composable
fun MetricItem(label: String, amount: Double, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        Text(
            text = "$${amount.toInt()}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun RentCard(
    rent: RentPayment,
    onMarkPaid: () -> Unit,
    onUpdateStatus: (PaymentStatus) -> Unit,
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
                    text = rent.propertyTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(
                        containerColor = when (rent.status) {
                            PaymentStatus.PAID -> MaterialTheme.colorScheme.primaryContainer
                            PaymentStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                            PaymentStatus.OVERDUE -> MaterialTheme.colorScheme.errorContainer
                        }
                    ) {
                        Text(
                            text = rent.status.name,
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
                                text = { Text("Mark Paid") },
                                onClick = {
                                    showMenu = false
                                    onMarkPaid()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mark Pending") },
                                onClick = {
                                    showMenu = false
                                    onUpdateStatus(PaymentStatus.PENDING)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mark Overdue") },
                                onClick = {
                                    showMenu = false
                                    onUpdateStatus(PaymentStatus.OVERDUE)
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
                text = "Tenant: ${rent.tenantName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Amount: $${rent.amount.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Due: ${rent.dueDate}" + if (rent.paidDate != null) " • Paid: ${rent.paidDate}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (rent.status != PaymentStatus.PAID) {
                    Button(onClick = onMarkPaid) {
                        Text("Mark Paid")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRentPaymentDialog(
    tenants: List<com.example.cat2mobileapplication.data.model.Tenant>,
    properties: List<com.example.cat2mobileapplication.data.model.Property>,
    onDismiss: () -> Unit,
    onAdd: (RentPayment) -> Unit
) {
    var selectedTenant by remember { mutableStateOf(tenants.firstOrNull()) }
    var selectedProperty by remember { mutableStateOf(properties.firstOrNull()) }
    var amount by remember { mutableStateOf(selectedProperty?.monthlyRent?.toString() ?: "2000") }
    var dueDate by remember { mutableStateOf("2025-04-01") }
    var status by remember { mutableStateOf(PaymentStatus.PENDING) }
    var errorText by remember { mutableStateOf<String?>(null) }

    var tenantExpanded by remember { mutableStateOf(false) }
    var propertyExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Rent Payment") },
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
                                    amount = property.monthlyRent.toInt().toString()
                                    propertyExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTenant == null || selectedProperty == null || amount.isBlank() || dueDate.isBlank()) {
                        errorText = "Please fill in all fields"
                        return@Button
                    }
                    val amountVal = amount.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0.0) {
                        errorText = "Invalid amount"
                        return@Button
                    }
                    val payment = RentPayment(
                        id = "r_${UUID.randomUUID().toString().take(6)}",
                        tenantId = selectedTenant!!.id,
                        tenantName = selectedTenant!!.name,
                        propertyTitle = selectedProperty!!.title,
                        amount = amountVal,
                        dueDate = dueDate.trim(),
                        paidDate = null,
                        status = status
                    )
                    onAdd(payment)
                }
            ) {
                Text("Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
