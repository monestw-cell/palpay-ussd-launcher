package com.palpay.ussdlauncher.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palpay.ussdlauncher.R
import com.palpay.ussdlauncher.ui.theme.BankPalestineGreen
import com.palpay.ussdlauncher.ui.theme.JawwalRed
import com.palpay.ussdlauncher.ui.theme.PalPayGray

data class ServiceItem(
    val key: String,
    val nameRes: Int,
    val iconRes: Int,
    val color: Color,
    val inactive: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (String) -> Unit,
    onRecipientsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val services = listOf(
        ServiceItem("bank_palestine", R.string.bank_palestine, R.drawable.ic_bank_palestine, BankPalestineGreen),
        ServiceItem("jawwal_pay", R.string.jawwal_pay, R.drawable.ic_jawwal_pay, JawwalRed),
        ServiceItem("pal_pay", R.string.pal_pay, R.drawable.ic_pal_pay, PalPayGray, inactive = true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "اختر خدمة التحويل",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                services.forEach { service ->
                    ServiceCard(
                        service = service,
                        onClick = {
                            if (!service.inactive) onServiceClick(service.key)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomActionButton(
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    label = stringResource(R.string.recipients),
                    onClick = onRecipientsClick
                )
                BottomActionButton(
                    icon = { Icon(Icons.Default.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    label = stringResource(R.string.history),
                    onClick = onHistoryClick
                )
                BottomActionButton(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    label = stringResource(R.string.settings),
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
fun ServiceCard(service: ServiceItem, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (service.inactive) Color(0xFFF5F5F5) else service.color.copy(alpha = 0.12f))
                .clickable(enabled = !service.inactive) { onClick() }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (service.inactive) PalPayGray.copy(alpha = 0.3f) else service.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(service.iconRes),
                    contentDescription = stringResource(service.nameRes),
                    modifier = Modifier.size(52.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(service.nameRes),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = if (service.inactive) PalPayGray else service.color,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
        if (service.inactive) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PalPayGray,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.pal_pay_inactive),
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun BottomActionButton(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}
