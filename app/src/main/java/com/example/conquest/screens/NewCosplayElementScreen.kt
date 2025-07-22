package com.example.conquest.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.data.entity.CosplayElement
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class NewCosplayElementScreen(val cosplayId: Int)

@Composable
fun NewCosplayElementScreen(
    cosplayId: Int,
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var ready by remember { mutableStateOf(false) }
    var highlight by remember { mutableStateOf(false) }
    var bought by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.9f)
                .padding(top = 70.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Element",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Element Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(32.dp)
            )

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Cost (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("$") },
                shape = RoundedCornerShape(32.dp)
            )

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
                        checked = ready,
                        onCheckedChange = { ready = it },
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
                        checked = bought,
                        onCheckedChange = { bought = it },
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

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .statusBarsPadding()
                .size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = "Cancel",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FloatingActionButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val element = CosplayElement(
                            id = 0,
                            cosplayId = cosplayId,
                            name = name.trim(),
                            cost = cost.takeIf { it.isNotBlank() }?.toDoubleOrNull(),
                            ready = ready,
                            photoPath = null,
                            highlight = highlight,
                            bought = bought,
                            notes = null
                        )
                        cosplayViewModel.insertElement(element)
                        navController.popBackStack()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please fill out all required fields!"
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Save")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 140.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(32.dp),
                )
            })
    }
}