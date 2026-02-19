package com.example.aepbill.data

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(private val context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("aepbill_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ESP32_IP = "esp32_ip"
        private const val KEY_ESP32_PORT = "esp32_port"
        private const val DEFAULT_IP = "192.168.100.209"
        private const val DEFAULT_PORT = 443
    }

    var esp32Ip: String
        get() = prefs.getString(KEY_ESP32_IP, DEFAULT_IP) ?: DEFAULT_IP
        set(value) = prefs.edit().putString(KEY_ESP32_IP, value).apply()

    var esp32Port: Int
        get() = prefs.getInt(KEY_ESP32_PORT, DEFAULT_PORT)
        set(value) = prefs.edit().putInt(KEY_ESP32_PORT, value).apply()

    fun getBaseUrl(): String = "https://$esp32Ip:$esp32Port"
}
