package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.deleteStoredImageByPath
import com.example.conquest.components.saveImageUriToInternalStorage
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyInputField
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.MySwitchCard
import com.example.conquest.data.classes.ElementFormState
import com.example.conquest.ui.theme.UIConsts
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class NewElement(val cosplayId: Int)

@Composable
fun NewElement(
    cosplayId: Int,
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var form by remember { mutableStateOf(ElementFormState()) }
    var didCommit by remember { mutableStateOf(false) }

    val latestPhotoPath by rememberUpdatedState(form.photoPath)
    val latestDidCommit by rememberUpdatedState(didCommit)

    DisposableEffect(Unit) {
        onDispose {
            if (!latestDidCommit) {
                latestPhotoPath.takeIf { it.isNotBlank() }?.let { deleteStoredImageByPath(context, it) }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            saveImageUriToInternalStorage(
                context = context,
                uri = it,
                fileNamePrefix = "cosplay_element",
            ).onSuccess { savedPath ->
                val previousUnsavedPath = form.photoPath.takeIf {
                    it.isNotBlank() && it != savedPath
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

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Add Element")

            MyImageBox(
                photoPath = form.photoPath,
                contentDescription = "Selected image",
                size = UIConsts.imageSizeS,
                clickable = true,
                onClick = { launcher.launch("image/*") },
            )

            MyInputField(
                value = form.name,
                onValueChange = { form = form.copy(name = it) },
                label = "Element Name*",
                singleLine = true,
                shape = RoundedCornerShape(UIConsts.cornerRadiusL),
            )

            MyInputField(
                value = form.cost,
                onValueChange = { form = form.copy(cost = it) },
                label = "Cost (Optional)",
                singleLine = true,
                filterDecimal = true,
                shape = RoundedCornerShape(UIConsts.cornerRadiusL),
            )

            MySwitchCard(
                label = "Ready",
                checked = form.ready,
                onCheckedChange = { form = form.copy(ready = it) },
            )

            MySwitchCard(
                label = "Bought",
                checked = form.bought,
                onCheckedChange = { form = form.copy(bought = it) },
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = {
                didCommit = true
                cosplayViewModel.insertElement(
                    form.toEntity(
                        cosplayId = cosplayId, id = 0, notes = null
                    )
                )
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}