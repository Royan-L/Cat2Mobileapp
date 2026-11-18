package com.example.cat2mobileapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cat2mobileapplication.ui.components.AppScaffold
import com.example.cat2mobileapplication.ui.theme.Cat2MobileApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cat2MobileApplicationTheme {
                AppScaffold()
            }
        }
    }
}
