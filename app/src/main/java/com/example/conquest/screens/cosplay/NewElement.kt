package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
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
            try {
                val fileName = "cosplay_element_${System.currentTimeMillis()}.jpg"
                val savedPath = copyUriToInternalStorage(context, it, fileName)
                form = form.copy(photoPath = savedPath)
            } catch (e: Exception) {
                // Validation/snackbar is handled by MySaveCancelRow.
                // For image failures we still show a snackbar immediately.
                // (No need to pass down a scope; we'll launch locally.)
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to save image: ${e.localizedMessage}")
                }
            }
        }
    }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Add Element")

            OutlinedTextField(
                value = form.name,
                onValueChange = { form = form.copy(name = it) },
                label = { Text("Element Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(32.dp)
            )

            OutlinedTextField(
                value = form.cost,
                onValueChange = { form = form.copy(cost = it.filter { c -> c.isDigit() || c == '.' }) },
                label = { Text("Cost (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("$") },
                shape = RoundedCornerShape(32.dp)
            )

            MyImageBox(
                photoPath = form.photoPath,
                contentDescription = "Selected image",
                size = 64.dp,
                clickable = true,
                onClick = { launcher.launch("image/*") },
            )

            // Ready switch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Ready")
                    Switch(
                        checked = form.ready,
                        onCheckedChange = { form = form.copy(ready = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.secondary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Bought")
                    Switch(
                        checked = form.bought,
                        onCheckedChange = { form = form.copy(bought = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.secondary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = { cosplayViewModel.insertElement(form.toEntity(cosplayId = cosplayId, id = 0, notes = null)) },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}