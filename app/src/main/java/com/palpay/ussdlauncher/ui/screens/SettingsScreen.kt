package com.palpay.ussdlauncher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.palpay.ussdlauncher.R
import com.palpay.ussdlauncher.ui.theme.ThemePreference
import com.palpay.ussdlauncher.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedSim by remember { mutableIntStateOf(viewModel.getDefaultSim()) }
    var pin by remember { mutableStateOf(viewModel.getPin()) }
    var bankPalestineCode by remember { mutableStateOf(viewModel.getBankPalestineCode()) }
    var jawwalPayCode by remember { mutableStateOf(viewModel.getJawwalPayCode()) }
    var accessibilityEnabled by remember { mutableStateOf(viewModel.isAccessibilityEnabled()) }
    val isServiceRunning = remember { mutableStateOf(viewModel.isAccessibilityServiceRunning(context)) }
    val themePreference by viewModel.themePreference.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val running = viewModel.isAccessibilityServiceRunning(context)
                isServiceRunning.value = running
                if (running && !accessibilityEnabled) {
                    accessibilityEnabled = true
                    viewModel.setAccessibilityEnabled(true)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val simOptions = listOf(
        0 to stringResource(R.string.sim_auto),
        1 to stringResource(R.string.sim_1),
        2 to stringResource(R.string.sim_2)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SettingsSection(title = stringResource(R.string.theme_section)) {
                val themeOptions = listOf(
                    ThemePreference.SYSTEM to stringResource(R.string.theme_system),
                    ThemePreference.LIGHT to stringResource(R.string.theme_light),
                    ThemePreference.DARK to stringResource(R.string.theme_dark)
                )
                themeOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = themePreference == value,
                            onClick = { viewModel.setThemePreference(value) }
                        )
                        Text(label, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            HorizontalDivider()

            SettingsSection(title = stringResource(R.string.default_sim)) {
                simOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSim == value,
                            onClick = {
                                selectedSim = value
                                viewModel.saveDefaultSim(value)
                            }
                        )
                        Text(label, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                if (selectedSim != 0) {
                    Text(
                        text = "ملاحظة: اختيار الشريحة يعتمد على تطبيق الهاتف. ACTION_DIAL قد لا يدعم فرض الشريحة في جميع الأجهزة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            HorizontalDivider()

            SettingsSection(title = stringResource(R.string.pin_settings)) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text(stringResource(R.string.enter_pin)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.savePin(pin) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save_pin))
                }
            }

            HorizontalDivider()

            SettingsSection(title = stringResource(R.string.ussd_codes_section)) {
                Text(
                    text = "اتركه فارغاً لاستخدام الكود الأساسي. المتغيرات المدعومة: {phone} {amount} {pin}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bankPalestineCode,
                    onValueChange = { bankPalestineCode = it },
                    label = { Text(stringResource(R.string.bank_palestine_code)) },
                    placeholder = { Text(stringResource(R.string.code_hint), style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.saveBankPalestineCode(bankPalestineCode) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حفظ كود بنك فلسطين")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = jawwalPayCode,
                    onValueChange = { jawwalPayCode = it },
                    label = { Text(stringResource(R.string.jawwal_pay_code)) },
                    placeholder = { Text("مثال: *110*{phone}*{amount}#", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.saveJawwalPayCode(jawwalPayCode) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حفظ كود جوال باي")
                }
            }

            HorizontalDivider()

            SettingsSection(title = stringResource(R.string.accessibility_section)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.enable_accessibility), fontWeight = FontWeight.Medium)
                        Text(
                            if (isServiceRunning.value) stringResource(R.string.accessibility_enabled)
                            else stringResource(R.string.accessibility_not_enabled),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isServiceRunning.value) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    }
                    Switch(
                        checked = accessibilityEnabled,
                        onCheckedChange = {
                            accessibilityEnabled = it
                            viewModel.setAccessibilityEnabled(it)
                        }
                    )
                }

                if (accessibilityEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = stringResource(R.string.accessibility_explanation),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.openAccessibilitySettings(context)
                                    isServiceRunning.value = viewModel.isAccessibilityServiceRunning(context)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.open_accessibility_settings))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}
