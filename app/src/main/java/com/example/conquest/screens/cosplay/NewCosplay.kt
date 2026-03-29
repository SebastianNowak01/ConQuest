package com.example.conquest.screens.cosplay

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyInputField
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.MySwitchCard
import com.example.conquest.data.classes.CosplayFormState

@Serializable
object NewCosplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCosplay(
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    var form by remember { mutableStateOf(CosplayFormState()) }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "New Project")

            MySwitchCard(
                label = if (form.inProgress) "In Progress" else "Planned",
                checked = form.inProgress,
                onCheckedChange = { form = form.copy(inProgress = it) })

            MyInputField(
                value = form.characterName,
                onValueChange = { form = form.copy(characterName = it) },
                label = "Character Name*",
                singleLine = true,
            )

            MyInputField(
                value = form.series,
                onValueChange = { form = form.copy(series = it) },
                label = "Series*",
                singleLine = true,
            )

            DatePickerFieldToModal(
                label = "Initial date*",
                selectedDate = form.initialDate,
                onDateSelected = { form = form.copy(initialDate = it) })

            DatePickerFieldToModal(
                label = "Due date",
                selectedDate = form.dueDate,
                onDateSelected = { form = form.copy(dueDate = it) })

            // Note: MyInputField currently doesn't include a leading icon ("$").
            // If you want that too, we can extend MyInputField with an optional leadingIcon slot.
            MyInputField(
                value = form.budget,
                onValueChange = { form = form.copy(budget = it) },
                label = "Budget (Optional)",
                singleLine = true,
                filterDecimal = true,
            )

        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = { cosplayViewModel.insertCosplay(form.toEntity(uid = 0, finished = false)) },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
