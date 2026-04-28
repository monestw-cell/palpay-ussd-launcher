package com.palpay.ussdlauncher.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.palpay.ussdlauncher.data.db.AppDatabase
import com.palpay.ussdlauncher.data.db.entity.Recipient
import com.palpay.ussdlauncher.data.db.entity.Transfer
import com.palpay.ussdlauncher.data.prefs.SecurePrefsManager
import com.palpay.ussdlauncher.repository.AppRepository
import com.palpay.ussdlauncher.service.CapturedUssdData
import com.palpay.ussdlauncher.service.UssdCaptureBus
import com.palpay.ussdlauncher.ui.theme.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TransferType {
    FRIEND,
    MERCHANT,
    OWN_ACCOUNTS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val securePrefs: SecurePrefsManager

    val recipients: kotlinx.coroutines.flow.Flow<List<Recipient>>
    val transfers: kotlinx.coroutines.flow.Flow<List<Transfer>>

    private val _capturedUssdInfo = MutableStateFlow<CapturedUssdData?>(null)
    val capturedUssdInfo: StateFlow<CapturedUssdData?> = _capturedUssdInfo.asStateFlow()

    private val _showSaveRecipientDialog = MutableStateFlow(false)
    val showSaveRecipientDialog: StateFlow<Boolean> = _showSaveRecipientDialog.asStateFlow()

    private val _lastDialedPhone = MutableStateFlow("")
    val lastDialedPhone: StateFlow<String> = _lastDialedPhone.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _themePreference = MutableStateFlow(ThemePreference.SYSTEM)
    val themePreference: StateFlow<ThemePreference> = _themePreference.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db)
        securePrefs = SecurePrefsManager(application)

        recipients = repository.allRecipients
        transfers = repository.allTransfers

        _themePreference.value = when (securePrefs.getThemePreference()) {
            "LIGHT" -> ThemePreference.LIGHT
            "DARK" -> ThemePreference.DARK
            else -> ThemePreference.SYSTEM
        }

        viewModelScope.launch {
            UssdCaptureBus.events.collect { data ->
                _capturedUssdInfo.value = data
            }
        }
    }

    fun addRecipient(name: String, phone: String) {
        viewModelScope.launch {
            repository.addRecipient(name.trim(), phone.trim())
            showSnackbar("تم حفظ المستفيد بنجاح")
        }
    }

    fun updateRecipient(recipient: Recipient) {
        viewModelScope.launch {
            repository.updateRecipient(recipient)
            showSnackbar("تم تحديث المستفيد")
        }
    }

    fun deleteRecipient(recipient: Recipient) {
        viewModelScope.launch {
            repository.deleteRecipient(recipient)
            showSnackbar("تم حذف المستفيد")
        }
    }

    fun deleteTransfer(transfer: Transfer) {
        viewModelScope.launch { repository.deleteTransfer(transfer) }
    }

    fun clearAllTransfers() {
        viewModelScope.launch {
            repository.clearAllTransfers()
            showSnackbar("تم مسح سجل التحويلات")
        }
    }

    fun launchUssd(
        context: Context,
        serviceKey: String,
        phone: String,
        amount: String,
        recipientName: String,
        transferType: TransferType? = null
    ) {
        val ussdCode = buildUssdCode(serviceKey, phone, amount, transferType)
        _lastDialedPhone.value = phone

        if (transferType != TransferType.OWN_ACCOUNTS) {
            viewModelScope.launch {
                repository.addTransfer(
                    recipientName = recipientName.ifBlank { phone },
                    phone = phone,
                    amount = amount,
                    serviceKey = serviceKey
                )
            }
        }

        val encodedCode = ussdCode.replace("#", Uri.encode("#"))
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encodedCode")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        if (transferType != TransferType.OWN_ACCOUNTS) {
            _showSaveRecipientDialog.value = true
        }
    }

    private fun buildUssdCode(
        serviceKey: String,
        phone: String,
        amount: String,
        transferType: TransferType? = null
    ): String {
        val pin = securePrefs.getPin() ?: ""
        val customCode = when (serviceKey) {
            "bank_palestine" -> securePrefs.getBankPalestineCode()
            "jawwal_pay" -> securePrefs.getJawwalPayCode()
            else -> ""
        }

        if (customCode.isNotBlank()) {
            return customCode
                .replace("{phone}", phone)
                .replace("{amount}", amount)
                .replace("{pin}", pin)
        }

        return when (serviceKey) {
            "jawwal_pay" -> buildJawwalPayCode(phone, amount, transferType ?: TransferType.FRIEND)
            "bank_palestine" -> buildBankPalestineCode(phone, amount, pin, transferType ?: TransferType.FRIEND)
            else -> "*267#"
        }
    }

    private fun buildJawwalPayCode(phone: String, amount: String, transferType: TransferType): String {
        return when (transferType) {
            TransferType.FRIEND -> "*110*1*$phone*$amount#"
            TransferType.MERCHANT -> "*110*2*$phone*$amount#"
            TransferType.OWN_ACCOUNTS -> "*110*3#"
        }
    }

    private fun buildBankPalestineCode(phone: String, amount: String, pin: String, transferType: TransferType): String {
        return when (transferType) {
            TransferType.FRIEND -> if (pin.isNotBlank()) "*267*1*$phone*$amount*$pin#" else "*267*1*$phone*$amount#"
            TransferType.MERCHANT -> "*267*2*$phone*$amount#"
            TransferType.OWN_ACCOUNTS -> "*267*3#"
        }
    }

    fun previewUssdCode(
        serviceKey: String,
        transferType: TransferType,
        phone: String,
        amount: String
    ): String {
        val customCode = when (serviceKey) {
            "bank_palestine" -> securePrefs.getBankPalestineCode()
            "jawwal_pay" -> securePrefs.getJawwalPayCode()
            else -> ""
        }
        if (customCode.isNotBlank()) return ""

        val displayPhone = if (phone.isBlank()) "PHONE" else phone
        val displayAmount = if (amount.isBlank()) "AMOUNT" else amount
        val pin = securePrefs.getPin() ?: ""

        return when (serviceKey) {
            "jawwal_pay" -> buildJawwalPayCode(displayPhone, displayAmount, transferType)
            "bank_palestine" -> buildBankPalestineCode(displayPhone, displayAmount, pin, transferType)
            else -> ""
        }
    }

    fun dismissSaveRecipientDialog() {
        _showSaveRecipientDialog.value = false
    }

    fun savePin(pin: String) {
        securePrefs.savePin(pin)
        showSnackbar("تم حفظ الرقم السري بأمان")
    }

    fun getPin(): String = securePrefs.getPin() ?: ""

    fun saveDefaultSim(sim: Int) {
        securePrefs.saveDefaultSim(sim)
    }

    fun getDefaultSim(): Int = securePrefs.getDefaultSim()

    fun saveBankPalestineCode(code: String) {
        securePrefs.saveBankPalestineCode(code)
        showSnackbar("تم حفظ الكود")
    }

    fun saveJawwalPayCode(code: String) {
        securePrefs.saveJawwalPayCode(code)
        showSnackbar("تم حفظ الكود")
    }

    fun getBankPalestineCode(): String = securePrefs.getBankPalestineCode()
    fun getJawwalPayCode(): String = securePrefs.getJawwalPayCode()

    fun setThemePreference(theme: ThemePreference) {
        _themePreference.value = theme
        securePrefs.saveThemePreference(theme.name)
    }

    fun setAccessibilityEnabled(enabled: Boolean) {
        securePrefs.setAccessibilityEnabled(enabled)
    }

    fun isAccessibilityEnabled(): Boolean = securePrefs.isAccessibilityEnabled()

    fun isAccessibilityServiceRunning(context: Context): Boolean {
        val serviceName = "${context.packageName}/.service.UssdAccessibilityService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(serviceName)
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun clearCapturedInfo() {
        _capturedUssdInfo.value = null
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
