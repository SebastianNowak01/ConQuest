package com.maeldev.conquest.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                size = UIConsts.imageSizeM,
                clickable = true,
                onClick = { imageLauncher.launch() },
                emptyContentDescription = "Pick cosplay photo",
            )

            MySwitchCard(
                label = if (form.inProgress) "In Progress" else "Planned",
                checked = form.inProgress,
                onCheckedChange = { onFormChange(form.copy(inProgress = it)) }
            )

            MyInputField(
                value = form.characterName,
                onValueChange = { onFormChange(form.copy(characterName = it)) },
                label = "Character Name*",
                singleLine = true,
            )

            MyInputField(
                value = form.series,
                onValueChange = { onFormChange(form.copy(series = it)) },
                label = "Series*",
                singleLine = true,
            )

            DatePickerFieldToModal(
                label = "Initial date*",
                selectedDate = form.initialDate,
                onDateSelected = { onFormChange(form.copy(initialDate = it)) }
            )

            DatePickerFieldToModal(
                label = "Due date",
                selectedDate = form.dueDate,
                onDateSelected = { onFormChange(form.copy(dueDate = it)) }
            )

            MyInputField(
                value = form.budget,
                onValueChange = { onFormChange(form.copy(budget = it)) },
                label = "Budget (Optional)",
                singleLine = true,
                filterDecimal = true,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = onCancel,
            onCommit = onCommit,
            postCommit = postCommit,
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
