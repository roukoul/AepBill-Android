package com.example.aepbill.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.* 
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aepbill.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val ipAddress by viewModel.ipAddress.collectAsState()
    val port by viewModel.port.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val isTestingConnection by viewModel.isTestingConnection.collectAsState()
    
    val wifiNetworks by viewModel.wifiNetworks.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WiFi & Connection Configuration",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // WiFi Scan Section
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Scan Nearby WiFi Networks", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.scanWifi() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isScanning
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isScanning) "Scanning..." else "Scan for Networks")
                }

                if (wifiNetworks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select a Network:", style = MaterialTheme.typography.bodySmall)
                    wifiNetworks.forEach { network ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { ssid = network.ssid }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(network.ssid, modifier = Modifier.weight(1f))
                            Text("${network.rssi} dBm", style = MaterialTheme.typography.bodySmall)
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = ssid,
            onValueChange = { ssid = it },
            label = { Text("WiFi SSID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("WiFi Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = ipAddress,
            onValueChange = { viewModel.updateIpAddress(it) },
            label = { Text("ESP32 IP Address / Hostname") },
            placeholder = { Text("aepbill.local or 192.168.4.1") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = port,
            onValueChange = { viewModel.updatePort(it) },
            label = { Text("HTTPS Port") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.testConnection() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTestingConnection
        ) {
            if (isTestingConnection) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isTestingConnection) "Connecting..." else "Test & Save Connection")
        }

        Spacer(modifier = Modifier.height(24.dp))

        connectionStatus?.let { status ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (status.isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = status.message,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (status.isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }
    }
}
