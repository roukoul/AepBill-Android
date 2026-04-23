package com.example.aepbill.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aepbill.App
import com.example.aepbill.data.model.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    data class Connected(
        val status: Status,
        val health: com.example.aepbill.data.model.HealthResponse? = null,
        val power: com.example.aepbill.data.model.PowerResponse? = null
    ) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

class MainViewModel : ViewModel() {

    private val espRepository = App.instance.espRepository

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                refreshStatus()
                delay(10000) // 10 seconds - less intrusive
            }
        }
    }

    fun refreshStatus() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // Only show Connecting state if we're disconnected/error
            // Prevents flickering when already connected
            if (_connectionState.value !is ConnectionState.Connected) {
                _connectionState.value = ConnectionState.Connecting
            }

            try {
                val status = espRepository.getStatus()
                val health = espRepository.getHealth()
                val power = espRepository.getPower().getOrNull()
                _connectionState.value = ConnectionState.Connected(status, health, power)
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Error(
                    e.message ?: "Unknown error occurred"
                )
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
