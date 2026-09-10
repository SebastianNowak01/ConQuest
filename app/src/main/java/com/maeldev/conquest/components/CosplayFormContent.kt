package com.maeldev.conquest.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.maeldev.conquest.data.classes.CosplayFormState
import com.maeldev.conquest.theme.UIConsts
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosplayFormContent(
    title: String,
    form: CosplayFormState,
    originalPhotoPath: String?,
    didCommit: Boolean,
    onFormChange: (CosplayFormState) -> Unit,
    snackbarHostState: SnackbarHostState,
    onCancel: () -> Unit,
    onCommit: () -> Unit,
    postCommit: () -> Unit,
    isDirty: Boolean = false,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showErrors by remember { mutableStateOf(false) }
    val cancel = rememberDiscardChangesGuard(isDirty = isDirty, onDiscard = onCancel)

    DiscardUnsavedImageEffect(
        context = context,
        currentPhotoPath = form.cosplayPhotoPath,
        originalPhotoPath = originalPhotoPath,
        isCommitted = didCommit
    )

    val imageLauncher = pickAndSaveImageLauncher(
        context = context,
        fileNamePrefix = "cosplay_cover",
        onSaved = { savedPath ->
            val previousUnsavedPath = form.cosplayPhotoPath.takeIf {
                it.isNotBlank() && it != originalPhotoPath && it != savedPath
            }
            previousUnsavedPath?.let {
                deleteStoredImageByPath(context, it)
            }
            onFormChange(form.copy(cosplayPhotoPath = savedPath))
        },
        onError = { error ->
            scope.launch {
                snackbarHostState.showSnackbar("Failed to save image: ${error.localizedMessage}")
            }
        }
    )

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = title)

            MyImageBox(
                photoPath = form.cosplayPhotoPath,
                contentDescription = "Selected cosplay photo",
                size = UIConsts.imageSizeL,
                shape = RoundedCornerShape(UIConsts.cornerRadiusM),
                clickable = true,
                onClick = { imageLauncher.launch() },
                emptyContentDescription = "Pick cosplay photo",
                showEditBadge = true,
                onClear = {
                    discardUnsavedImage(context, form.cosplayPhotoPath, originalPhotoPath)
                    onFormChange(form.copy(cosplayPhotoPath = ""))
                },
            )

            MySectionLabel(text = "Status")

            MyStatusSegmentedRow(
                selected = form.status,
                onStatusChange = { onFormChange(form.copy(status = it)) },
            )

            MyInputField(
                value = form.characterName,
                onValueChange = { onFormChange(form.copy(characterName = it)) },
                label = "Character Name*",
                singleLine = true,
                isError = showErrors && form.characterName.isBlank(),
                errorMessage = "Character name is required",
            )

            MyInputField(
                value = form.series,
                onValueChange = { onFormChange(form.copy(series = it)) },
                label = "Series*",
                singleLine = true,
                isError = showErrors && form.series.isBlank(),
                errorMessage = "Series is required",
            )

            DatePickerFieldToModal(
                label = "Initial date*",
                selectedDate = form.initialDate,
                onDateSelected = { onFormChange(form.copy(initialDate = it)) },
                isError = showErrors && form.initialDate == null,
                errorMessage = "Initial date is required",
            )

            DatePickerFieldToModal(
                label = "Due date",
                selectedDate = form.dueDate,
                onDateSelected = { onFormChange(form.copy(dueDate = it)) },
                onClear = { onFormChange(form.copy(dueDate = null)) },
            )

            MyInputField(
                value = form.budget,
                onValueChange = { onFormChange(form.copy(budget = it)) },
                label = "Budget",
                singleLine = true,
                filterDecimal = true,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onInvalidAttempt = { showErrors = true },
            onCancel = cancel,
            onCommit = onCommit,
            postCommit = postCommit,
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
