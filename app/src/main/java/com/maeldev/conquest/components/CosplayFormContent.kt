package com.maeldev.conquest.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.maeldev.conquest.data.classes.CosplayFormState
import com.maeldev.conquest.data.classes.FormActions
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
    actions: FormActions,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showErrors by remember { mutableStateOf(false) }
    val cancel = rememberDiscardChangesGuard(isDirty = actions.isDirty, onDiscard = actions.onCancel)

    DiscardUnsavedImageEffect(
        context = context,
        currentPhotoPath = form.cosplayPhotoPath,
        originalPhotoPath = originalPhotoPath,
        isCommitted = didCommit,
    )

    val imageLauncher =
        pickAndSaveImageLauncher(
            context = context,
            fileNamePrefix = "cosplay_cover",
            onSaved = { savedPath ->
                val previousUnsavedPath =
                    form.cosplayPhotoPath.takeIf {
                        it.isNotBlank() && it != originalPhotoPath && it != savedPath
                    }
                previousUnsavedPath?.let {
                    deleteStoredImageByPath(context, it)
                }
                onFormChange(form.copy(cosplayPhotoPath = savedPath))
            },
            onError = { error ->
                scope.launch {
                    actions.snackbarHostState.showSnackbar("Failed to save image: ${error.localizedMessage}")
                }
            },
        )

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = title)

            MyImageBox(
                photoPath = form.cosplayPhotoPath,
                clickable = true,
                onClick = { imageLauncher.launch() },
                onClear = {
                    discardUnsavedImage(context, form.cosplayPhotoPath, originalPhotoPath)
                    onFormChange(form.copy(cosplayPhotoPath = ""))
                },
                config =
                    MyImageBoxConfig(
                        size = UIConsts.imageSizeL,
                        shape = RoundedCornerShape(UIConsts.cornerRadiusM),
                        contentDescription = "Selected cosplay photo",
                        emptyContentDescription = "Pick cosplay photo",
                        showEditBadge = true,
                    ),
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
                options =
                    InputFieldOptions(
                        singleLine = true,
                    ),
                error =
                    FieldError(
                        isError = showErrors && form.characterName.isBlank(),
                        message = "Character name is required",
                    ),
            )

            MyInputField(
                value = form.series,
                onValueChange = { onFormChange(form.copy(series = it)) },
                label = "Series*",
                options =
                    InputFieldOptions(
                        singleLine = true,
                    ),
                error =
                    FieldError(
                        isError = showErrors && form.series.isBlank(),
                        message = "Series is required",
                    ),
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
                options =
                    InputFieldOptions(
                        singleLine = true,
                        filterDecimal = true,
                    ),
            )
        }

        MySaveCancelRow(
            snackbarHostState = actions.snackbarHostState,
            isValid = form.isValid,
            actions =
                SaveCancelActions(
                    onCancel = cancel,
                    onCommit = actions.onCommit,
                    postCommit = actions.postCommit,
                    onInvalidAttempt = { showErrors = true },
                ),
        )

        MySnackbarHost(hostState = actions.snackbarHostState)
    }
}
