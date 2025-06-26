package com.example.conquest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.lifecycle.lifecycleScope
import com.example.conquest.components.Drawer
import com.example.conquest.ui.theme.ConQuestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = CosplayViewModel(application).database
                val allCosplays = db.cosplayDao().getAllCosplays()
                Log.d("DATABASE", "Contents: $allCosplays")
            }
        }

        enableEdgeToEdge()
        setContent {
            ConQuestTheme {
                Scaffold() {
                    Drawer()
                }
            }
        }
    }
}

