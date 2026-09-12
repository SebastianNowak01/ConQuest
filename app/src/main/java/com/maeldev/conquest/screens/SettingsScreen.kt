package com.maeldev.conquest.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyButton
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySnackbarHost
import com.maeldev.conquest.components.menuBorder
import com.maeldev.conquest.components.menuContainerColor
import com.maeldev.conquest.components.menuShape
import com.maeldev.conquest.theme.UIConsts
import com.maeldev.conquest.viewmodel.ExportImportState
import com.maeldev.conquest.viewmodel.ExportImportViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object SettingsScreenParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val selectedOption by rememberThemePreference(context)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val exportImportViewModel: ExportImportViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val importState by exportImportViewModel.exportImportState.collectAsState()

    val options = listOf("dark", "light", "automatic")
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(importState) {
        val message =
            when (val state = importState) {
                is ExportImportState.Success -> "Cosplays imported"
                is ExportImportState.Error -> "Import failed: ${state.message}"
                else -> null
            } ?: return@LaunchedEffect

        exportImportViewModel.resetState()
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    MyOuterBox {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = UIConsts.paddingL),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MySettingsSectionLabel(text = "Appearance")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = selectedOption.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Theme") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier =
                        Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(UIConsts.cornerRadiusL),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            errorContainerColor = MaterialTheme.colorScheme.background,
                        ),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    shape = menuShape,
                    containerColor = menuContainerColor,
                    border = menuBorder,
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
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(UIConsts.paddingM))

            MySettingsSectionLabel(text = "Your data")

            val openDocumentLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                ) { uri ->
                    if (uri != null) {
                        exportImportViewModel.importCosplays(uri)
                    }
                }

            MyButton(
                text = "Export Cosplays",
                onClick = { navController.navigate(ExportSelectionScreen) },
            )

            Spacer(modifier = Modifier.padding(UIConsts.paddingS))

            MyButton(
                text = "Import Cosplays",
                onClick = { openDocumentLauncher.launch(arrayOf("application/zip")) },
            )

            if (importState is ExportImportState.Loading) {
                Spacer(modifier = Modifier.padding(UIConsts.paddingS))
                CircularProgressIndicator()
            }
        }

        MySnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun MySettingsSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier =
            Modifier
                .fillMaxWidth(UIConsts.columnWidthFraction)
                .padding(bottom = UIConsts.paddingS),
    )
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val DARK_MODE_KEY = stringPreferencesKey("dark_mode_option")

suspend fun setDarkModeOption(
    context: Context,
    option: String,
) {
    context.dataStore.edit { prefs ->
        prefs[DARK_MODE_KEY] = option
    }
}

fun getDarkModeOption(context: Context): Flow<String> =
    context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: "automatic"
    }

@Composable
fun rememberThemePreference(context: Context): State<String> {
    val flow = remember { getDarkModeOption(context) }
    return flow.collectAsState(initial = "automatic")
}
