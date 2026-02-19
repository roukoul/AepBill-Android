package com.example.aepbill.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aepbill.App
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.aepbill.data.model.WifiNetwork
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConnectionStatus(
    val isSuccess: Boolean,
    val message: String
)

class SettingsViewModel : ViewModel() {

    private val settingsRepository = App.instance.settingsRepository
    private val espRepository = App.instance.espRepository

    private val _ipAddress = MutableStateFlow(settingsRepository.esp32Ip)
    val ipAddress: StateFlow<String> = _ipAddress.asStateFlow()

    private val _port = MutableStateFlow(settingsRepository.esp32Port.toString())
    val port: StateFlow<String> = _port.asStateFlow()

    private val _connectionStatus = MutableStateFlow<ConnectionStatus?>(null)
    val connectionStatus: StateFlow<ConnectionStatus?> = _connectionStatus.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    private val _wifiNetworks = MutableStateFlow<List<WifiNetwork>>(emptyList())
    val wifiNetworks: StateFlow<List<WifiNetwork>> = _wifiNetworks.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun scanWifi() {
        viewModelScope.launch {
            _isScanning.value = true
            try {
                val result = espRepository.getWifiScan()
                if (result.isSuccess) {
                    _wifiNetworks.value = result.getOrNull()?.networks ?: emptyList()
                } else {
                    _connectionStatus.value = ConnectionStatus(false, "❌ Scan failed")
                }
            } catch (e: Exception) {
                _connectionStatus.value = ConnectionStatus(false, "❌ Scan error: ${e.message}")
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun updateIpAddress(ip: String) {
        _ipAddress.value = ip
    }

    fun updatePort(portStr: String) {
        _port.value = portStr
    }

    fun testConnection() {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _connectionStatus.value = null

            try {
                settingsRepository.esp32Ip = _ipAddress.value
                settingsRepository.esp32Port = _port.value.toIntOrNull() ?: 443

                val status = espRepository.getStatus()
                
                _connectionStatus.value = ConnectionStatus(
                    isSuccess = true,
                    message = "✅ Connection successful! ESP32 time: ${status.time}"
                )
            } catch (e: Exception) {
                _connectionStatus.value = ConnectionStatus(
                    isSuccess = false,
                    message = "❌ Connection failed: ${e.message ?: "Unknown error"}"
                )
            } finally {
                _isTestingConnection.value = false
            }
        }
    }

    fun saveSettings() {
        settingsRepository.esp32Ip = _ipAddress.value
        settingsRepository.esp32Port = _port.value.toIntOrNull() ?: 443
        
        _connectionStatus.value = ConnectionStatus(
            isSuccess = true,
            message = "✅ Settings saved successfully!"
        )
    }
}
