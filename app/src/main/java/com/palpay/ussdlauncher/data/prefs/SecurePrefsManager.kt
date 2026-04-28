package com.palpay.ussdlauncher.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePrefsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "ussd_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val regularPrefs: SharedPreferences =
        context.getSharedPreferences("ussd_regular_prefs", Context.MODE_PRIVATE)

    fun savePin(pin: String) {
        encryptedPrefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(): String? = encryptedPrefs.getString(KEY_PIN, null)

    fun clearPin() {
        encryptedPrefs.edit().remove(KEY_PIN).apply()
    }

    fun saveDefaultSim(sim: Int) {
        regularPrefs.edit().putInt(KEY_DEFAULT_SIM, sim).apply()
    }

    fun getDefaultSim(): Int = regularPrefs.getInt(KEY_DEFAULT_SIM, 0)

    fun saveBankPalestineCode(code: String) {
        regularPrefs.edit().putString(KEY_BANK_PALESTINE_CODE, code).apply()
    }

    fun getBankPalestineCode(): String =
        regularPrefs.getString(KEY_BANK_PALESTINE_CODE, "") ?: ""

    fun saveJawwalPayCode(code: String) {
        regularPrefs.edit().putString(KEY_JAWWAL_PAY_CODE, code).apply()
    }

    fun getJawwalPayCode(): String =
        regularPrefs.getString(KEY_JAWWAL_PAY_CODE, "") ?: ""

    fun setAccessibilityEnabled(enabled: Boolean) {
        regularPrefs.edit().putBoolean(KEY_ACCESSIBILITY_ENABLED, enabled).apply()
    }

    fun isAccessibilityEnabled(): Boolean =
        regularPrefs.getBoolean(KEY_ACCESSIBILITY_ENABLED, false)

    fun saveThemePreference(theme: String) {
        regularPrefs.edit().putString(KEY_THEME_PREFERENCE, theme).apply()
    }

    fun getThemePreference(): String =
        regularPrefs.getString(KEY_THEME_PREFERENCE, "SYSTEM") ?: "SYSTEM"

    companion object {
        private const val KEY_PIN = "user_pin"
        private const val KEY_DEFAULT_SIM = "default_sim"
        private const val KEY_BANK_PALESTINE_CODE = "bank_palestine_code"
        private const val KEY_JAWWAL_PAY_CODE = "jawwal_pay_code"
        private const val KEY_ACCESSIBILITY_ENABLED = "accessibility_enabled"
        private const val KEY_THEME_PREFERENCE = "theme_preference"
    }
}
