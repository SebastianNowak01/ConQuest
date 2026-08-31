package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                size = UIConsts.imageSizeM,
                clickable = true,
                onClick = { imageLauncher.launch() },
            )

            MyInputField(
                value = form.name,
                onValueChange = { onFormChange(form.copy(name = it)) },
                label = "Name*",
                singleLine = true,
                shape = RoundedCornerShape(UIConsts.cornerRadiusL),
            )

            MyInputField(
                value = form.cost,
                onValueChange = { onFormChange(form.copy(cost = it)) },
                label = "Cost (Optional)",
                singleLine = true,
                filterDecimal = true,
                shape = RoundedCornerShape(UIConsts.cornerRadiusL),
            )

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
            onCancel = onCancel,
            onCommit = onCommit,
            postCommit = postCommit,
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
