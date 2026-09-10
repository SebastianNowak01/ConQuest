package com.maeldev.conquest.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyCosplayRow
import com.maeldev.conquest.components.MyExportSelectionModeFabs
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.components.MySnackbarHost
import com.maeldev.conquest.viewmodel.CosplayViewModel
import com.maeldev.conquest.viewmodel.ExportImportState
import com.maeldev.conquest.viewmodel.ExportImportViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
object ExportSelectionScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSelectionScreen(navController: NavController) {
    val cosplayViewModel: CosplayViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val exportImportViewModel: ExportImportViewModel = viewModel(factory = AppViewModelProvider.Factory)
    
    val cosplays by cosplayViewModel.allCosplays.collectAsState()
    val exportState by exportImportViewModel.exportImportState.collectAsState()

    val selection = rememberSelectionState(items = cosplays, id = { it.uid })

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportImportState.Success -> {
                exportImportViewModel.resetState()
                navController.popBackStack()
            }
            is ExportImportState.Error -> {
                // Stay on the screen so the selection is preserved and the export can be retried.
                exportImportViewModel.resetState()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Export failed: ${state.message}")
                }
            }
            else -> Unit
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null && selection.isActive) {
            exportImportViewModel.exportCosplays(selection.selectedIds, uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Cosplays to Export") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        MyOuterBox(modifier = Modifier.padding(paddingValues)) {
            if (selection.isActive) {
                MyExportSelectionModeFabs(
                    selection = selection,
                    onExportSelection = {
                        val dateString = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                        val selectedIds = selection.selectedIds
                        val fileName = if (selectedIds.size == 1) {
                            val name = cosplays.first { it.uid == selectedIds.first() }.name.replace(" ", "_")
                            "${name}_$dateString.zip"
                        } else {
                            "ConQuest_Export_$dateString.zip"
                        }
                        createDocumentLauncher.launch(fileName)
                    },
                )
            }

            MyLazyColumn(
                items = cosplays,
                key = { it.uid },
                isSelected = { selection.isSelected(it.uid) },
                // Unlike the other lists a plain tap selects here, since this screen exists
                // only to pick cosplays for export.
                onClick = { cosplay -> selection.toggle(cosplay.uid) },
                onLongClick = { cosplay -> selection.select(cosplay.uid) },
            ) { cosplay ->
                MyCosplayRow(
                    name = cosplay.name,
                    series = cosplay.series,
                    photoPath = cosplay.cosplayPhotoPath ?: "",
                )
            }

            MySnackbarHost(hostState = snackbarHostState)
        }
    }
}
