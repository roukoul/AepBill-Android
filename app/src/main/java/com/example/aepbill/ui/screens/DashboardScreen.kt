package com.example.aepbill.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aepbill.ui.components.GaugeComponent
import com.example.aepbill.ui.components.GradientButton
import com.example.aepbill.ui.theme.*
import com.example.aepbill.ui.viewmodels.ConnectionState
import com.example.aepbill.ui.viewmodels.MainViewModel

@OptIn(ExperimentalTextApi::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel = viewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Linear Progress Bar at the top during refresh
            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(4.dp).padding(bottom = 8.dp),
                    color = PrimaryBlue,
                    trackColor = SurfaceDark
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Header with Gradient Text
            Text(
                text = "لوحة تحكم AepBill",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(
                        colors = PrimaryGradientColors,
                        tileMode = TileMode.Clamp
                    )
                ),
                modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
            )

            when (val state = connectionState) {
                is ConnectionState.Connected -> {
                    val status = state.status

                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Main Gauge - REMOVED in Classic
                            Spacer(modifier = Modifier.height(32.dp))

                            // Status Grid (Relays, Time, Wifi, Alarm)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Relays Row 1: S and A
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoCard(
                                        title = "المرحل S",
                                        value = if (status.relay) "ON" else "OFF",
                                        gradient = if (status.relay) SuccessGradientColors else DangerGradientColors,
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoCard(
                                        title = "المرحل A",
                                        value = if (status.relay2) "ON" else "OFF",
                                        gradient = if (status.relay2) SuccessGradientColors else DangerGradientColors,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Relays Row 2: B and C
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoCard(
                                        title = "المرحل B",
                                        value = if (status.relay3) "ON" else "OFF",
                                        gradient = if (status.relay3) SuccessGradientColors else DangerGradientColors,
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoCard(
                                        title = "المرحل C",
                                        value = if (status.relay4) "ON" else "OFF",
                                        gradient = if (status.relay4) SuccessGradientColors else DangerGradientColors,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // System Info Row 1: Time and Alarm
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoCard(
                                        title = "الوقت",
                                        value = status.time.substringAfter(" "), // Show only time
                                        gradient = PrimaryGradientColors,
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoCard(
                                        title = "المنبه القادم",
                                        value = status.nextAlarm ?: "--:--",
                                        gradient = PrimaryGradientColors,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // System Info Row 2: Wifi Mode
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoCard(
                                        title = "وضع WiFi",
                                        value = status.wifiMode,
                                        gradient = WarningGradientColors,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            
                            // Anomaly Warning Card (only show if anomaly detected)
                            // Anomaly Warning Card - REMOVED in Classic
                            
                            Spacer(modifier = Modifier.height(32.dp))

                            // Control Buttons
                            GradientButton(
                                text = "تحديث البيانات",
                                onClick = { viewModel.refreshStatus() }
                            )
                        }
                    }
                }
                
                is ConnectionState.Connecting -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 100.dp),
                        color = PrimaryBlue
                    )
                }

                is ConnectionState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.refreshStatus() })
                }

                is ConnectionState.Disconnected -> {
                     ErrorState(message = "غير متصل", onRetry = { viewModel.refreshStatus() })
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = Color.Transparent, // Transparent because we use brush
                style = TextStyle(
                     brush = Brush.linearGradient(gradient)
                ).merge(MaterialTheme.typography.titleLarge)
            )
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 80.dp)
    ) {
        Text(
            text = "⚠️ مشكلة في الاتصال",
            style = MaterialTheme.typography.headlineSmall,
            color = AlarmRed,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        GradientButton(
            text = "إعادة المحاولة", 
            onClick = onRetry,
            gradient = DangerGradientColors
        )
    }
}
