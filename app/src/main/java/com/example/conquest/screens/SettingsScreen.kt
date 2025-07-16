package com.example.conquest.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.PopupProperties
import androidx.compose.material3.MenuAnchorType

@Serializable
object SettingsScreenParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val selectedOption by rememberThemePreference(context)
    val coroutineScope = rememberCoroutineScope()

    val options = listOf("dark", "light", "automatic")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(MaterialTheme.colorScheme.background),

        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedOption.replaceFirstChar { it.uppercase() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Theme") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(32.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                    ),
                properties = PopupProperties(clippingEnabled = true)
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            coroutineScope.launch {
                                setDarkModeOption(context, selectionOption)
                            }
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val DARK_MODE_KEY = stringPreferencesKey("dark_mode_option")

suspend fun setDarkModeOption(context: Context, option: String) {
    context.dataStore.edit { prefs ->
        prefs[DARK_MODE_KEY] = option
    }
}

fun getDarkModeOption(context: Context): Flow<String> = context.dataStore.data.map { prefs ->
    prefs[DARK_MODE_KEY] ?: "automatic"
}

@Composable
fun rememberThemePreference(context: Context): State<String> {
    val flow = remember { getDarkModeOption(context) }
    return flow.collectAsState(initial = "automatic")
}