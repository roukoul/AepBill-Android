package com.example.aepbill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aepbill.ui.navigation.AppNavigation
import com.example.aepbill.ui.theme.AepBillTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AepBillTheme {
                AppNavigation()
            }
        }
    }
}
