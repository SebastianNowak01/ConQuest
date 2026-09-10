package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
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
import com.maeldev.conquest.theme.UIConsts
import kotlinx.coroutines.launch

@Composable
fun ElementFormContent(
    title: String,
    form: ElementFormState,
    originalPhotoPath: String?,
    didCommit: Boolean,
    onFormChange: (ElementFormState) -> Unit,
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
        currentPhotoPath = form.photoPath,
        originalPhotoPath = originalPhotoPath,
        isCommitted = didCommit
    )

    val imageLauncher = pickAndSaveImageLauncher(
        context = context,
        fileNamePrefix = "cosplay_element",
        onSaved = { savedPath ->
            val previousUnsavedPath = form.photoPath.takeIf {
                it.isNotBlank() && it != originalPhotoPath && it != savedPath
            }
            previousUnsavedPath?.let {
                deleteStoredImageByPath(context, it)
            }
            onFormChange(form.copy(photoPath = savedPath))
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
                photoPath = form.photoPath,
                contentDescription = "Element image",
                size = UIConsts.imageSizeL,
                shape = RoundedCornerShape(UIConsts.cornerRadiusM),
                clickable = true,
                onClick = { imageLauncher.launch() },
                emptyContentDescription = "Pick element photo",
                showEditBadge = true,
                onClear = {
                    discardUnsavedImage(context, form.photoPath, originalPhotoPath)
                    onFormChange(form.copy(photoPath = ""))
                },
            )

            MyInputField(
                value = form.name,
                onValueChange = { onFormChange(form.copy(name = it)) },
                label = "Name*",
                singleLine = true,
                shape = RoundedCornerShape(UIConsts.cornerRadiusL),
                isError = showErrors && form.name.isBlank(),
                errorMessage = "Name is required",
            )

            MyInputField(
                value = form.cost,
                onValueChange = { onFormChange(form.copy(cost = it)) },
                label = "Cost",
                singleLine = true,
                filterDecimal = true,
                shape = RoundedCornerShape(UIConsts.cornerRadiusL),
            )

            MySectionLabel(text = "Status")

            Row(
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MySwitchCard(
                    label = "Ready",
                    checked = form.ready,
                    onCheckedChange = { onFormChange(form.copy(ready = it)) },
                    modifier = Modifier.weight(1f)
                )

                MySwitchCard(
                    label = "Bought",
                    checked = form.bought,
                    onCheckedChange = { onFormChange(form.copy(bought = it)) },
                    modifier = Modifier.weight(1f)
                )
            }

            MyInputField(
                value = form.notes,
                onValueChange = { onFormChange(form.copy(notes = it)) },
                label = "Notes",
                singleLine = false,
                maxLines = 6,
                height = UIConsts.heightM,
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
