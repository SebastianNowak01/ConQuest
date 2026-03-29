package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            saveImageUriToInternalStorage(
                context = context,
                uri = it,
                fileNamePrefix = "cosplay_element",
            ).onSuccess { savedPath ->
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

            MyInputField(
                value = form.name,
                onValueChange = { form = form.copy(name = it) },
                label = "Element Name*",
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
            )

            // Note: MyInputField currently doesn't include a leading icon ("$").
            // If you want that too, we can extend MyInputField with an optional leadingIcon slot.
            MyInputField(
                value = form.cost,
                onValueChange = { form = form.copy(cost = it) },
                label = "Cost (Optional)",
                singleLine = true,
                filterDecimal = true,
                shape = RoundedCornerShape(32.dp),
            )

            MyImageBox(
                photoPath = form.photoPath,
                contentDescription = "Selected image",
                size = 64.dp,
                clickable = true,
                onClick = { launcher.launch("image/*") },
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