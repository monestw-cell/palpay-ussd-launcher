package com.palpay.ussdlauncher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.palpay.ussdlauncher.R
import com.palpay.ussdlauncher.data.db.entity.Recipient
import com.palpay.ussdlauncher.viewmodel.MainViewModel
import com.palpay.ussdlauncher.viewmodel.TransferType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
    serviceKey: String,
    viewModel: MainViewModel,
    preselectedPhone: String = "",
    preselectedName: String = "",
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val recipients by viewModel.recipients.collectAsState(initial = emptyList())

    var phone by remember { mutableStateOf(preselectedPhone) }
    var recipientName by remember { mutableStateOf(preselectedName) }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedTransferType by remember { mutableStateOf(TransferType.FRIEND) }

    val showSaveDialog by viewModel.showSaveRecipientDialog.collectAsState()
    val capturedInfo by viewModel.capturedUssdInfo.collectAsState()

    val hasCustomCode = when (serviceKey) {
        "bank_palestine" -> viewModel.getBankPalestineCode().isNotBlank()
        "jawwal_pay" -> viewModel.getJawwalPayCode().isNotBlank()
        else -> false
    }

    val serviceName = when (serviceKey) {
        "bank_palestine" -> "بنك فلسطين"
        "jawwal_pay" -> "جوال باي"
        else -> "خدمة USSD"
    }

    val useGuidedMode = !hasCustomCode && (serviceKey == "bank_palestine" || serviceKey == "jawwal_pay")

    val transferOptions = listOf(
        TransferType.FRIEND to "صديق",
        TransferType.MERCHANT to "تاجر",
        TransferType.OWN_ACCOUNTS to "بين الحسابات"
    )

    val needsPhone = selectedTransferType != TransferType.OWN_ACCOUNTS
    val needsAmount = selectedTransferType != TransferType.OWN_ACCOUNTS

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$serviceName - ${stringResource(R.string.send_money)}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (useGuidedMode) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "نوع التحويل",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            transferOptions.forEach { (type, label) ->
                                FilterChip(
                                    selected = selectedTransferType == type,
                                    onClick = { selectedTransferType = type },
                                    label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        val ussdPreview = viewModel.previewUssdCode(
                            serviceKey = serviceKey,
                            transferType = selectedTransferType,
                            phone = phone,
                            amount = amount
                        )
                        if (ussdPreview.isNotBlank()) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "الكود: $ussdPreview",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            if (needsPhone || !useGuidedMode) {
                Text(
                    text = "اختر من المستفيدين المحفوظين",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                if (recipients.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = if (recipientName.isNotBlank()) "$recipientName - $phone" else stringResource(R.string.select_recipient),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.select_recipient)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            recipients.forEach { recipient ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(recipient.name, fontWeight = FontWeight.Medium)
                                            Text(recipient.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        }
                                    },
                                    onClick = {
                                        phone = recipient.phone
                                        recipientName = recipient.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = stringResource(R.string.or_enter_manually),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(R.string.phone_number)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }

            if (needsAmount || !useGuidedMode) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    suffix = { Text("₪") }
                )
            }

            if (useGuidedMode && selectedTransferType == TransferType.OWN_ACCOUNTS) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "سيتم فتح قائمة $serviceName لإجراء التحويل بين حساباتك الخاصة.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val isEnabled = if (useGuidedMode) {
                when (selectedTransferType) {
                    TransferType.OWN_ACCOUNTS -> true
                    else -> phone.isNotBlank()
                }
            } else {
                phone.isNotBlank()
            }

            Button(
                onClick = {
                    if (isEnabled) {
                        viewModel.launchUssd(
                            context = context,
                            serviceKey = serviceKey,
                            phone = phone,
                            amount = amount,
                            recipientName = recipientName,
                            transferType = if (useGuidedMode) selectedTransferType else null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = isEnabled
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.transfer), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSaveDialog) {
        SaveRecipientDialog(
            suggestedPhone = phone,
            suggestedName = capturedInfo?.name ?: "",
            onSave = { name ->
                viewModel.addRecipient(name, phone)
                viewModel.dismissSaveRecipientDialog()
                viewModel.clearCapturedInfo()
            },
            onDismiss = {
                viewModel.dismissSaveRecipientDialog()
                viewModel.clearCapturedInfo()
            }
        )
    }
}

@Composable
fun SaveRecipientDialog(
    suggestedPhone: String,
    suggestedName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(suggestedName) }
    var userEdited by remember { mutableStateOf(false) }

    LaunchedEffect(suggestedName) {
        if (!userEdited && suggestedName.isNotBlank()) {
            name = suggestedName
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.save_recipient_title), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.save_recipient_message))
                Text(suggestedPhone, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        userEdited = true
                    },
                    label = { Text(stringResource(R.string.recipient_name_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (suggestedName.isNotBlank() && !userEdited) {
                    Text(
                        text = stringResource(R.string.captured_recipient),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name) },
                enabled = name.isNotBlank()
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
