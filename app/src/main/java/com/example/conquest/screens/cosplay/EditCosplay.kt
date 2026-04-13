package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyInputField
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.MySwitchCard
import com.example.conquest.components.saveImageUriToInternalStorage
import com.example.conquest.data.classes.CosplayFormState
import com.example.conquest.ui.theme.UIConsts
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class EditCosplay(val cosplayId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCosplay(
    cosplayId: Int,
    navController: NavController,
    cosplayViewModel: CosplayViewModel = viewModel(),
) {
    val cosplay by cosplayViewModel.getCosplayById(cosplayId).collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var form by remember { mutableStateOf(CosplayFormState()) }

    LaunchedEffect(cosplay?.uid) {
        cosplay?.let { loaded ->
            form = CosplayFormState.fromEntity(loaded)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            saveImageUriToInternalStorage(
                context = context,
                uri = it,
                fileNamePrefix = "cosplay_cover",
            ).onSuccess { savedPath ->
                form = form.copy(cosplayPhotoPath = savedPath)
            }.onFailure { error ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Failed to save image: ${error.localizedMessage}",
                    )
                }
            }
        }
    }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Cosplay")

            MySwitchCard(
                label = if (form.inProgress) "In Progress" else "Planned",
                checked = form.inProgress,
                onCheckedChange = { form = form.copy(inProgress = it) },
            )

            MyInputField(
                value = form.characterName,
                onValueChange = { form = form.copy(characterName = it) },
                label = "Character Name*",
                singleLine = true,
            )

            MyInputField(
                value = form.series,
                onValueChange = { form = form.copy(series = it) },
                label = "Series*",
                singleLine = true,
            )

            MyImageBox(
                photoPath = form.cosplayPhotoPath,
                contentDescription = "Selected cosplay photo",
                size = UIConsts.imageSizeM,
                clickable = true,
                onClick = { launcher.launch("image/*") },
                emptyContentDescription = "Pick cosplay photo",
            )

            DatePickerFieldToModal(
                label = "Initial date*",
                selectedDate = form.initialDate,
                onDateSelected = { form = form.copy(initialDate = it) },
            )

            DatePickerFieldToModal(
                label = "Due date",
                selectedDate = form.dueDate,
                onDateSelected = { form = form.copy(dueDate = it) },
            )

            MyInputField(
                value = form.budget,
                onValueChange = { form = form.copy(budget = it) },
                label = "Budget (Optional)",
                singleLine = true,
                filterDecimal = true,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = {
                val current = cosplay ?: return@MySaveCancelRow
                cosplayViewModel.updateCosplay(form.toUpdatedEntity(current))
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}

