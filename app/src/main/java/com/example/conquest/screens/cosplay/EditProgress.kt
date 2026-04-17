package com.example.conquest.screens.cosplay

import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyInputField
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.ui.theme.UIConsts
import kotlinx.serialization.Serializable

@Serializable
data class EditProgressPhoto(
    val photoId: Int,
    val cosplayId: Int,
)

@Composable
fun EditProgressPhoto(
    photoId: Int,
    cosplayId: Int,
    navController: NavController,
    cosplayViewModel: CosplayViewModel = viewModel(),
) {
    val photo by cosplayViewModel.getProgressPhotoById(photoId, cosplayId).collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(photo?.id) {
        notes = photo?.notes.orEmpty()
    }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Progress Note")

            MyImageBox(
                photoPath = photo?.path.orEmpty(),
                contentDescription = "Progress photo",
                size = UIConsts.heightM,
                clickable = true,
                onClick = {},
                previewWhenPhotoExists = true,
            )

            MyInputField(
                value = notes,
                onValueChange = { notes = it },
                label = "Note",
                singleLine = false,
                maxLines = 6,
                height = UIConsts.heightM,
            )

            TextButton(onClick = {
                val current = photo ?: return@TextButton
                cosplayViewModel.updateProgressPhoto(current.copy(notes = null))
                notes = ""
            }) {
                Text("Delete Note")
            }
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = true,
            onCancel = { navController.popBackStack() },
            onCommit = {
                val current = photo ?: return@MySaveCancelRow
                cosplayViewModel.updateProgressPhoto(current.copy(notes = notes.ifBlank { null }))
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}

