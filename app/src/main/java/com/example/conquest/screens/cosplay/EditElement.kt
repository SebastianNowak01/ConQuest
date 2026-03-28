package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.saveImageUriToInternalStorage
import com.example.conquest.data.classes.ElementFormState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class EditElement(val elementId: Int)

@Composable
fun EditElement(
    elementId: Int, navController: NavController, cosplayViewModel: CosplayViewModel = viewModel()
) {
    val context = LocalContext.current
    val element by cosplayViewModel.getElementById(elementId).collectAsState(initial = null)

    var form by remember { mutableStateOf(ElementFormState()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            saveImageUriToInternalStorage(
                context = context,
                uri = uri,
                fileNamePrefix = "cosplay_element",
            ).onSuccess { savedPath ->
                form = form.copy(photoPath = savedPath)
            }.onFailure { e ->
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to save image: ${e.localizedMessage}")
                }
            }
        }
    }

    LaunchedEffect(element) {
        element?.let {
            form = ElementFormState(
                name = it.name,
                cost = it.cost?.toString() ?: "",
                ready = it.ready,
                bought = it.bought,
                photoPath = it.photoPath ?: "",
                highlight = it.highlight,
                notes = it.notes ?: "",
            )
        }
    }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Element")

            MyImageBox(
                photoPath = form.photoPath,
                contentDescription = "Element image",
                size = 80.dp,
                clickable = true,
                onClick = { imagePickerLauncher.launch("image/*") },
            )

            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = form.name,
                onValueChange = { form = form.copy(name = it) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = form.cost,
                onValueChange = {
                    form = form.copy(cost = it.filter { c -> c.isDigit() || c == '.' })
                },
                label = { Text("Cost") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )

            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ready switch
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Ready")
                        Switch(
                            checked = form.ready,
                            onCheckedChange = { form = form.copy(ready = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Bought")
                        Switch(
                            checked = form.bought,
                            onCheckedChange = { form = form.copy(bought = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }

            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = form.notes,
                onValueChange = { form = form.copy(notes = it) },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(20.dp),
                maxLines = 6
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = {
                val current = element ?: return@MySaveCancelRow
                val updatedElement = form.toEntity(
                    cosplayId = current.cosplayId,
                    id = current.id,
                ).copy(
                    // Preserve any fields that are NOT part of the element form.
                    highlight = current.highlight,
                )
                cosplayViewModel.updateElement(updatedElement)
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
