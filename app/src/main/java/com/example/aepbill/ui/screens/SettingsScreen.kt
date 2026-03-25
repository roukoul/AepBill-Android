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
            text = "إعدادات الاتصال و WiFi",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // WiFi Scan Section
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("البحث عن شبكات WiFi قريبة", style = MaterialTheme.typography.titleMedium)
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
                    Text(if (isScanning) "جاري البحث..." else "البحث عن الشبكات")
                }

                if (wifiNetworks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("اختر شبكة:", style = MaterialTheme.typography.bodySmall)
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
            label = { Text("اسم الشبكة (SSID)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة مرور WiFi") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = ipAddress,
            onValueChange = { viewModel.updateIpAddress(it) },
            label = { Text("عنوان IP أو اسم المضيف للجهاز") },
            placeholder = { Text("aepbill.local أو 192.168.4.1") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = port,
            onValueChange = { viewModel.updatePort(it) },
            label = { Text("منفذ HTTPS") },
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
            Text(if (isTestingConnection) "جاري الاتصال..." else "اختبار وحفظ الاتصال")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        // Energy Management Section
        val powerSettings by viewModel.powerSettings.collectAsState()
        val isUpdatingPower by viewModel.isUpdatingPower.collectAsState()

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚡ إدارة الطاقة (Eco-Energy)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                var selectedMode by remember(powerSettings) { mutableStateOf(powerSettings?.mode ?: 0) }
                var startHour by remember(powerSettings) { mutableStateOf(powerSettings?.startHour ?: 23) }
                var startMin by remember(powerSettings) { mutableStateOf(powerSettings?.startMin ?: 0) }
                var endHour by remember(powerSettings) { mutableStateOf(powerSettings?.endHour ?: 6) }
                var endMin by remember(powerSettings) { mutableStateOf(powerSettings?.endMin ?: 0) }

                Text("وضع التشغيل:", style = MaterialTheme.typography.bodyMedium)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedMode == 0, onClick = { selectedMode = 0 })
                        Text("عادي (Normal)", modifier = Modifier.clickable { selectedMode = 0 })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedMode == 1, onClick = { selectedMode = 1 })
                        Text("توفير (Modem Sleep)", modifier = Modifier.clickable { selectedMode = 1 })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedMode == 2, onClick = { selectedMode = 2 })
                        Text("Veille (Deep Sleep)", modifier = Modifier.clickable { selectedMode = 2 })
                    }
                }

                if (selectedMode == 2) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("وقت النوم التلقائي:", style = MaterialTheme.typography.bodySmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("من", style = MaterialTheme.typography.bodySmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = startHour.toString(),
                                    onValueChange = { startHour = it.toIntOrNull() ?: 0 },
                                    modifier = Modifier.width(60.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Text(":")
                                OutlinedTextField(
                                    value = startMin.toString(),
                                    onValueChange = { startMin = it.toIntOrNull() ?: 0 },
                                    modifier = Modifier.width(60.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("إلى", style = MaterialTheme.typography.bodySmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = endHour.toString(),
                                    onValueChange = { endHour = it.toIntOrNull() ?: 0 },
                                    modifier = Modifier.width(60.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Text(":")
                                OutlinedTextField(
                                    value = endMin.toString(),
                                    onValueChange = { endMin = it.toIntOrNull() ?: 0 },
                                    modifier = Modifier.width(60.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.updatePowerSettings(selectedMode, startHour, startMin, endHour, endMin) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdatingPower
                ) {
                    if (isUpdatingPower) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("حفظ إعدادات الطاقة")
                }
            }
        }

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
