package com.example.aepbill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aepbill.App
import com.example.aepbill.data.model.AlarmEntry
import com.example.aepbill.ui.theme.DarkBackground
import com.example.aepbill.ui.theme.PrimaryBlue
import com.example.aepbill.ui.theme.SurfaceDark
import com.example.aepbill.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

private val DAYS_EN = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
private val DAYS_AR = listOf("الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت", "الأحد")

@Composable
fun AlarmsScreen() {
    val scope = rememberCoroutineScope()
    val espRepository = App.instance.espRepository
    
    var schedule by remember { mutableStateOf<Map<String, List<AlarmEntry>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val result = espRepository.getSchedule()
                result.onSuccess { scheduleResponse ->
                    schedule = scheduleResponse.schedule
                    isLoading = false
                }.onFailure { e ->
                    errorMessage = e.message
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Column {
            // Loading progress bar at the top
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = PrimaryBlue,
                    trackColor = SurfaceDark
                )
            }

            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Text(
                    text = "جدول التنبيهات",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
                
                // Refresh Button
                IconButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                val result = espRepository.getSchedule()
                                result.onSuccess { scheduleResponse ->
                                    schedule = scheduleResponse.schedule
                                    isLoading = false
                                }.onFailure { e ->
                                    errorMessage = e.message
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                        contentDescription = "تحديث",
                        tint = Color.White
                    )
                }
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 50.dp),
                        color = PrimaryBlue
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = "خطأ: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(DAYS_EN.zip(DAYS_AR)) { (dayEn, dayAr) ->
                            DayCard(
                                dayName = dayAr,
                                alarms = schedule[dayEn] ?: emptyList()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCard(dayName: String, alarms: List<AlarmEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "${alarms.size} تنبيهات",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (alarms.isNotEmpty()) SuccessGreen else Color.Gray
                )
            }
            
            if (alarms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                alarms.forEachIndexed { index, alarm ->
                    AlarmRow(index + 1, alarm)
                    if (index < alarms.size - 1) {
                        Divider(
                            color = Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "لا توجد تنبيهات",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AlarmRow(number: Int, alarm: AlarmEntry) {
    val relayName = when (alarm.relay) {
        0 -> "S"
        1 -> "A"
        2 -> "B"
        3 -> "C"
        else -> "${alarm.relay}"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$number",
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "مرحل $relayName | ${alarm.start} → ${alarm.end}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        if (alarm.endDay > 0) {
            Text(
                text = "+1 يوم",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Yellow
            )
        }
    }
}
