package com.palpay.ussdlauncher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.palpay.ussdlauncher.R
import com.palpay.ussdlauncher.data.db.entity.Transfer
import com.palpay.ussdlauncher.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val transfers by viewModel.transfers.collectAsState(initial = emptyList())
    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (transfers.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.clear_history), tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (transfers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.no_history), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transfers, key = { it.id }) { transfer ->
                    TransferCard(
                        transfer = transfer,
                        onDelete = { viewModel.deleteTransfer(transfer) }
                    )
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.clear_history)) },
            text = { Text(stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllTransfers()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.clear_history), color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun TransferCard(transfer: Transfer, onDelete: () -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val serviceDisplay = when (transfer.serviceKey) {
        "bank_palestine" -> "بنك فلسطين"
        "jawwal_pay" -> "جوال باي"
        else -> transfer.serviceKey
    }
    val statusColor = when (transfer.status) {
        "completed" -> Color(0xFF4CAF50)
        "failed" -> MaterialTheme.colorScheme.error
        else -> Color(0xFFFFA726)
    }
    val statusText = when (transfer.status) {
        "completed" -> stringResource(R.string.status_completed)
        "failed" -> stringResource(R.string.status_failed)
        else -> stringResource(R.string.status_pending)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(serviceDisplay, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = MaterialTheme.shapes.small, color = statusColor.copy(alpha = 0.15f)) {
                        Text(statusText, fontSize = MaterialTheme.typography.labelSmall.fontSize, color = statusColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Text("${stringResource(R.string.recipients)}: ${transfer.recipientName}", style = MaterialTheme.typography.bodyMedium)
                Text("${stringResource(R.string.phone_number)}: ${transfer.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                if (transfer.amount.isNotBlank()) {
                    Text("${stringResource(R.string.amount)}: ${transfer.amount} ₪", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                Text(dateFormatter.format(Date(transfer.timestamp)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
