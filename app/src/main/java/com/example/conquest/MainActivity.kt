package com.example.conquest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import com.example.conquest.components.Drawer
import com.example.conquest.ui.theme.ConQuestTheme
import androidx.compose.runtime.getValue
import com.example.conquest.screens.rememberThemePreference


class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themePref by rememberThemePreference(context)
            val darkTheme = when (themePref) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            ConQuestTheme(darkTheme = darkTheme) {
                Scaffold {
                    Drawer()
                }
            }
        }
    }
}
