package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.maeldev.conquest.data.classes.ElementFormState
import com.maeldev.conquest.data.classes.FormActions
import com.maeldev.conquest.theme.UIConsts
import kotlinx.coroutines.launch

@Composable
fun ElementFormContent(
    title: String,
    form: ElementFormState,
    originalPhotoPath: String?,
    didCommit: Boolean,
    onFormChange: (ElementFormState) -> Unit,
    actions: FormActions,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showErrors by remember { mutableStateOf(false) }
    val cancel = rememberDiscardChangesGuard(isDirty = actions.isDirty, onDiscard = actions.onCancel)

    DiscardUnsavedImageEffect(
        context = context,
        currentPhotoPath = form.photoPath,
        originalPhotoPath = originalPhotoPath,
        isCommitted = didCommit,
    )

    val imageLauncher =
        pickAndSaveImageLauncher(
            context = context,
            fileNamePrefix = "cosplay_element",
            onSaved = { savedPath ->
                val previousUnsavedPath =
                    form.photoPath.takeIf {
                        it.isNotBlank() && it != originalPhotoPath && it != savedPath
                    }
                previousUnsavedPath?.let {
                    deleteStoredImageByPath(context, it)
                }
                onFormChange(form.copy(photoPath = savedPath))
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
                photoPath = form.photoPath,
                clickable = true,
                onClick = { imageLauncher.launch() },
                onClear = {
                    discardUnsavedImage(context, form.photoPath, originalPhotoPath)
                    onFormChange(form.copy(photoPath = ""))
                },
                config =
                    MyImageBoxConfig(
                        size = UIConsts.imageSizeL,
                        shape = RoundedCornerShape(UIConsts.cornerRadiusM),
                        contentDescription = "Element image",
                        emptyContentDescription = "Pick element photo",
                        showEditBadge = true,
                    ),
            )

            MyInputField(
                value = form.name,
                onValueChange = { onFormChange(form.copy(name = it)) },
                label = "Name*",
                options =
                    InputFieldOptions(
                        singleLine = true,
                    ),
                error =
                    FieldError(
                        isError = showErrors && form.name.isBlank(),
                        message = "Name is required",
                    ),
            )

            MyInputField(
                value = form.cost,
                onValueChange = { onFormChange(form.copy(cost = it)) },
                label = "Cost",
                options =
                    InputFieldOptions(
                        singleLine = true,
                        filterDecimal = true,
                    ),
            )

            MySectionLabel(text = "Status")

            Row(
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MySwitchCard(
                    label = "Ready",
                    checked = form.ready,
                    onCheckedChange = { onFormChange(form.copy(ready = it)) },
                    modifier = Modifier.weight(1f),
                )

                MySwitchCard(
                    label = "Bought",
                    checked = form.bought,
                    onCheckedChange = { onFormChange(form.copy(bought = it)) },
                    modifier = Modifier.weight(1f),
                )
            }

            MyInputField(
                value = form.notes,
                onValueChange = { onFormChange(form.copy(notes = it)) },
                label = "Notes",
                options =
                    InputFieldOptions(
                        singleLine = false,
                        maxLines = 6,
                        height = UIConsts.heightM,
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
