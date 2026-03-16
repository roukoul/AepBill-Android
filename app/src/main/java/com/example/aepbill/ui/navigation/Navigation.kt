package com.example.aepbill.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aepbill.ui.screens.AboutScreen
import com.example.aepbill.ui.screens.AlarmsScreen
import com.example.aepbill.ui.screens.DashboardScreen
import com.example.aepbill.ui.screens.MenuScreen
import com.example.aepbill.ui.screens.SettingsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "لوحة التحكم", Icons.Default.Home)
    object Menu : Screen("menu", "القائمة", Icons.Default.Menu)
    object Settings : Screen("settings", "الإعدادات", Icons.Default.Settings)
    object About : Screen("about", "حول", Icons.Default.Info)
    object Alarms : Screen("alarms", "التنبيهات", Icons.Default.Notifications)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Bottom Bar items (Dashboard, Menu, Settings)
    val items = listOf(Screen.Dashboard, Screen.Menu, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = com.example.aepbill.ui.theme.SurfaceDark,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = com.example.aepbill.ui.theme.PrimaryBlue,
                            selectedTextColor = com.example.aepbill.ui.theme.PrimaryBlue,
                            unselectedIconColor = androidx.compose.ui.graphics.Color.Gray,
                            unselectedTextColor = androidx.compose.ui.graphics.Color.Gray,
                            indicatorColor = com.example.aepbill.ui.theme.SurfaceDark
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Menu.route) { 
                val context = androidx.compose.ui.platform.LocalContext.current
                val settingsRepository = com.example.aepbill.App.instance.settingsRepository
                MenuScreen(
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onNavigateToAlarms = { navController.navigate(Screen.Alarms.route) },
                    onOpenWebPage = { path ->
                        val baseUrl = "https://${settingsRepository.esp32Ip}:443"
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("$baseUrl$path")
                        )
                        context.startActivity(intent)
                    }
                ) 
            }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.About.route) { AboutScreen() }
            composable(Screen.Alarms.route) { AlarmsScreen() }
        }
    }
}
