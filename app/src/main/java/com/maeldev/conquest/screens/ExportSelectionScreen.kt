package com.maeldev.conquest.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyCosplayRow
import com.maeldev.conquest.components.MyEmptyState
import com.maeldev.conquest.components.MyExportSelectionModeFabs
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyListItemActions
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySnackbarHost
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.data.entity.Cosplay
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
        val state = exportState
        if (state !is ExportImportState.Success && state !is ExportImportState.Error) return@LaunchedEffect

        exportImportViewModel.resetState()
        if (state is ExportImportState.Error) {
            // Stay on the screen so the selection is preserved and the export can be retried.
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Export failed: ${state.message}")
            }
            return@LaunchedEffect
        }
        navController.popBackStack()
    }

    val createDocumentLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/zip"),
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
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        MyOuterBox(modifier = Modifier.padding(paddingValues)) {
            if (selection.isActive) {
                MyExportSelectionModeFabs(
                    selection = selection,
                    onExportSelection = {
                        createDocumentLauncher.launch(exportFileName(cosplays, selection.selectedIds))
                    },
                )
            }

            MyLazyColumn(
                items = cosplays,
                key = { it.uid },
                actions =
                    MyListItemActions(
                        isSelected = { selection.isSelected(it.uid) },
                        // Unlike the other lists a plain tap selects here, since this screen exists
                        // only to pick cosplays for export.
                        onClick = { cosplay -> selection.toggle(cosplay.uid) },
                        onLongClick = { cosplay -> selection.select(cosplay.uid) },
                    ),
            ) { cosplay ->
                SelectableCosplayRow(
                    cosplay = cosplay,
                    checked = selection.isSelected(cosplay.uid),
                    onToggle = { selection.toggle(cosplay.uid) },
                )
            }

            if (cosplays.isEmpty()) {
                MyEmptyState(
                    icon = Icons.Default.TheaterComedy,
                    title = "Nothing to export",
                    hint = "Cosplays you create will show here, ready to pick.",
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            MySnackbarHost(hostState = snackbarHostState)
        }
    }
}

/**
 * Unlike every other list in the app a plain tap selects here, so each row shows a checkbox —
 * otherwise nothing on screen says what tapping will do.
 */
@Composable
private fun SelectableCosplayRow(
    cosplay: Cosplay,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
        Box(modifier = Modifier.weight(1f)) {
            MyCosplayRow(cosplay = cosplay)
        }
    }
}

/**
 * Names the export archive after the single selected cosplay, or falls back to a generic name
 * when several are selected.
 */
private fun exportFileName(
    cosplays: List<Cosplay>,
    selectedIds: Set<Int>,
): String {
    val dateString = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    if (selectedIds.size != 1) return "ConQuest_Export_$dateString.zip"

    val name = cosplays.first { it.uid == selectedIds.first() }.name.replace(" ", "_")
    return "${name}_$dateString.zip"
}
