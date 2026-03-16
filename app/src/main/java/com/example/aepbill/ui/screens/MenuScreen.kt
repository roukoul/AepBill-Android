package com.example.aepbill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aepbill.App
import com.example.aepbill.ui.theme.DangerGradientColors
import com.example.aepbill.ui.theme.DarkBackground
import com.example.aepbill.ui.theme.PrimaryGradientColors
import com.example.aepbill.ui.theme.SuccessGradientColors
import com.example.aepbill.ui.theme.SurfaceDark
import com.example.aepbill.ui.theme.WarningGradientColors
import kotlinx.coroutines.launch

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val action: () -> Unit
)

@Composable
fun MenuScreen(
    onNavigateToAbout: () -> Unit,
    onNavigateToAlarms: () -> Unit = {},
    onOpenWebPage: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var showRestartDialog by remember { mutableStateOf(false) }
    var showFactoryResetDialog by remember { mutableStateOf(false) }
    var restartMessage by remember { mutableStateOf<String?>(null) }
    
    // Repository directly accessed for simplicity in Menu actions
    val espRepository = App.instance.espRepository

    val menuItems = listOf(
        MenuItem(
            title = "حول",
            icon = Icons.Default.Info,
            gradient = PrimaryGradientColors,
            action = onNavigateToAbout
        ),
        MenuItem(
            title = "إعدادات الجهاز",
            icon = Icons.Default.Settings,
            gradient = PrimaryGradientColors,
            action = { onOpenWebPage("/") }
        ),
        MenuItem(
            title = "إعدادات WiFi",
            icon = Icons.Default.Star,
            gradient = SuccessGradientColors,
            action = { onOpenWebPage("/wifiConfig") }
        ),
        MenuItem(
            title = "التنبيهات",
            icon = Icons.Default.Notifications,
            gradient = SuccessGradientColors,
            action = onNavigateToAlarms
        ),
        MenuItem(
            title = "إعادة التشغيل",
            icon = Icons.Default.Refresh,
            gradient = WarningGradientColors,
            action = { showRestartDialog = true }
        ),
        MenuItem(
            title = "ضبط المصنع",
            icon = Icons.Default.Delete,
            gradient = DangerGradientColors,
            action = { showFactoryResetDialog = true }
        )
    )

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text("إعادة تشغيل الجهاز؟") },
            text = { Text("هل أنت متأكد من رغبتك في إعادة تشغيل الجهاز؟ سيتم قطع الاتصال مؤقتا.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestartDialog = false
                        scope.launch {
                            restartMessage = "جاري إعادة التشغيل..."
                            val result = espRepository.restartDevice()
                            restartMessage = if (result.isSuccess) "تمت إعادة التشغيل! جاري إعادة الاتصال..." else "فشلت إعادة التشغيل!"
                        }
                    }
                ) {
                    Text("إعادة التشغيل", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) {
                    Text("إلغاء")
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.Gray
        )
    }

    if (restartMessage != null) {
        AlertDialog(
            onDismissRequest = { restartMessage = null },
            title = { Text("الحالة") },
            text = { Text(restartMessage!!) },
            confirmButton = {
                TextButton(onClick = { restartMessage = null }) { Text("موافق") }
            },
            containerColor = SurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showFactoryResetDialog) {
        AlertDialog(
            onDismissRequest = { showFactoryResetDialog = false },
            title = { Text("⚠️ ضبط المصنع؟") },
            text = { Text("سيؤدي هذا إلى حذف جميع التنبيهات وكلمات المرور وإعدادات WiFi. هل أنت متأكد؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFactoryResetDialog = false
                        scope.launch {
                            restartMessage = "جاري المسح..."
                            val result = espRepository.factoryReset()
                            restartMessage = if (result.isSuccess) "تمت إعادة الضبط! جاري التشغيل..." else "فشل إعادة الضبط!"
                        }
                    }
                ) {
                    Text("حذف الكل", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFactoryResetDialog = false }) {
                    Text("إلغاء")
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = Color.Red,
            textContentColor = Color.Gray
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "القائمة",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 24.dp).align(Alignment.CenterHorizontally)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    MenuCard(item)
                }
            }
        }
    }
}

@Composable
fun MenuCard(item: MenuItem) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .clickable { item.action() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(item.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
