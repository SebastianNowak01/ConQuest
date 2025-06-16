package com.example.conquest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.conquest.components.Navigation
import com.example.conquest.ui.theme.ConQuestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using lifecycleScope instead of Thread
        lifecycleScope.launch {
            // Switch to IO dispatcher for database operations
            withContext(Dispatchers.IO) {
                val db = ViewModel(application).database
                val allCosplays = db.cosplayDao().getAllCosplays()
                Log.d("DATABASE", "Contents: $allCosplays")
            }
        }

        enableEdgeToEdge()
        setContent {
            ConQuestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Navigation()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConQuestTheme {
        Navigation()
    }
}