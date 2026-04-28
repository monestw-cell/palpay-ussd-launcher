package com.palpay.ussdlauncher.data.prefs

import android.content.Context
import android.content.SharedPreferences

class SecurePrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ussd_app_prefs", Context.MODE_PRIVATE)

    fun savePin(pin: String) {
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(): String? = prefs.getString(KEY_PIN, null)

    fun clearPin() {
        prefs.edit().remove(KEY_PIN).apply()
    }

    fun saveDefaultSim(sim: Int) {
        prefs.edit().putInt(KEY_DEFAULT_SIM, sim).apply()
    }

    fun getDefaultSim(): Int = prefs.getInt(KEY_DEFAULT_SIM, 0)

    fun saveBankPalestineCode(code: String) {
        prefs.edit().putString(KEY_BANK_PALESTINE_CODE, code).apply()
    }

    fun getBankPalestineCode(): String =
        prefs.getString(KEY_BANK_PALESTINE_CODE, "") ?: ""

    fun saveJawwalPayCode(code: String) {
        prefs.edit().putString(KEY_JAWWAL_PAY_CODE, code).apply()
    }

    fun getJawwalPayCode(): String =
        prefs.getString(KEY_JAWWAL_PAY_CODE, "") ?: ""

    fun setAccessibilityEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ACCESSIBILITY_ENABLED, enabled).apply()
    }

    fun isAccessibilityEnabled(): Boolean =
        prefs.getBoolean(KEY_ACCESSIBILITY_ENABLED, false)

    fun saveThemePreference(theme: String) {
        prefs.edit().putString(KEY_THEME_PREFERENCE, theme).apply()
    }

    fun getThemePreference(): String =
        prefs.getString(KEY_THEME_PREFERENCE, "SYSTEM") ?: "SYSTEM"

    companion object {
        private const val KEY_PIN = "user_pin"
        private const val KEY_DEFAULT_SIM = "default_sim"
        private const val KEY_BANK_PALESTINE_CODE = "bank_palestine_code"
        private const val KEY_JAWWAL_PAY_CODE = "jawwal_pay_code"
        private const val KEY_ACCESSIBILITY_ENABLED = "accessibility_enabled"
        private const val KEY_THEME_PREFERENCE = "theme_preference"
    }
}
