package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyInputField
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.MySwitchCard
import com.example.conquest.components.deleteStoredImageByPath
import com.example.conquest.components.saveImageUriToInternalStorage
import com.example.conquest.data.classes.ElementFormState
import com.example.conquest.ui.theme.UIConsts
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
    var originalPhotoPath by remember { mutableStateOf<String?>(null) }
    var didCommit by remember { mutableStateOf(false) }

    val latestPhotoPath by rememberUpdatedState(form.photoPath)
    val latestOriginalPhotoPath by rememberUpdatedState(originalPhotoPath)
    val latestDidCommit by rememberUpdatedState(didCommit)

    DisposableEffect(Unit) {
        onDispose {
            if (!latestDidCommit) {
                val pendingPath = latestPhotoPath.takeIf {
                    it.isNotBlank() && it != latestOriginalPhotoPath
                }
                pendingPath?.let { deleteStoredImageByPath(context, it) }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            saveImageUriToInternalStorage(
                context = context,
                uri = uri,
                fileNamePrefix = "cosplay_element",
            ).onSuccess { savedPath ->
                val previousUnsavedPath = form.photoPath.takeIf {
                    it.isNotBlank() && it != originalPhotoPath && it != savedPath
                }
                previousUnsavedPath?.let { deleteStoredImageByPath(context, it) }
                form = form.copy(photoPath = savedPath)
            }.onFailure { e ->
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to save image: ${e.localizedMessage}")
                }
            }
        }
    }

    LaunchedEffect(element?.id) {
        element?.let { loaded ->
            form = ElementFormState.fromEntity(loaded)
            originalPhotoPath = loaded.photoPath
        }
    }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Element")

            MyImageBox(
                photoPath = form.photoPath,
                contentDescription = "Element image",
                size = UIConsts.imageSizeM,
                clickable = true,
                onClick = { imagePickerLauncher.launch("image/*") },
            )

            MyInputField(
                value = form.name,
                onValueChange = { form = form.copy(name = it) },
                label = "Name",
                singleLine = true,
            )

            MyInputField(
                value = form.cost,
                onValueChange = { form = form.copy(cost = it) },
                label = "Cost",
                singleLine = true,
                filterDecimal = true,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MySwitchCard(
                    label = "Ready",
                    checked = form.ready,
                    onCheckedChange = { form = form.copy(ready = it) },
                    modifier = Modifier.weight(1f)
                )

                MySwitchCard(
                    label = "Bought",
                    checked = form.bought,
                    onCheckedChange = { form = form.copy(bought = it) },
                    modifier = Modifier.weight(1f)
                )
            }

            MyInputField(
                value = form.notes,
                onValueChange = { form = form.copy(notes = it) },
                label = "Notes",
                singleLine = false,
                maxLines = 6,
                height = UIConsts.heightM,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = {
                val current = element ?: return@MySaveCancelRow
                val updated = form.toUpdatedEntity(current)
                val oldPath = current.photoPath
                val oldPathToDelete = if (updated.photoPath != oldPath) oldPath else null
                didCommit = true
                cosplayViewModel.updateElement(updated, oldPathToDelete = oldPathToDelete)
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
