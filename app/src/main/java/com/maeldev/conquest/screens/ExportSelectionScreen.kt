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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyCosplayRow
import com.maeldev.conquest.components.MyExportSelectionModeFabs
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.viewmodel.CosplayViewModel
import com.maeldev.conquest.viewmodel.ExportImportState
import com.maeldev.conquest.viewmodel.ExportImportViewModel
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

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(cosplays) {
        val visibleIds = cosplays.map { it.uid }.toSet()
        selectedIds = selectedIds.intersect(visibleIds)
        if (selectedIds.isEmpty()) {
            selectionMode = false
        }
    }

    LaunchedEffect(exportState) {
        if (exportState is ExportImportState.Success) {
            exportImportViewModel.resetState()
            navController.popBackStack()
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null && selectedIds.isNotEmpty()) {
            exportImportViewModel.exportCosplays(selectedIds, uri)
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
            if (selectionMode) {
                MyExportSelectionModeFabs(
                    onExitSelection = {
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    onExportSelection = {
                        val dateString = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                        val fileName = if (selectedIds.size == 1) {
                            val name = cosplays.first { it.uid == selectedIds.first() }.name.replace(" ", "_")
                            "${name}_$dateString.zip"
                        } else {
                            "ConQuest_Export_$dateString.zip"
                        }
                        createDocumentLauncher.launch(fileName)
                    },
                    onSelectAll = {
                        selectedIds = cosplays.map { it.uid }.toSet()
                        selectionMode = selectedIds.isNotEmpty()
                    }
                )
            }

            MyLazyColumn(
                items = cosplays,
                key = { it.uid },
                isSelected = { selectedIds.contains(it.uid) },
                onClick = { cosplay ->
                    if (!selectionMode) {
                        selectionMode = true
                        selectedIds = setOf(cosplay.uid)
                        return@MyLazyColumn
                    }
                    val id = cosplay.uid
                    selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                    if (selectedIds.isEmpty()) selectionMode = false
                },
                onLongClick = { cosplay ->
                    selectionMode = true
                    selectedIds = selectedIds + cosplay.uid
                },
            ) { cosplay ->
                MyCosplayRow(
                    name = cosplay.name,
                    series = cosplay.series,
                    photoPath = cosplay.cosplayPhotoPath ?: "",
                )
            }
        }
    }
}
