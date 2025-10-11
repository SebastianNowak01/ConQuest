package com.example.conquest.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.conquest.CosplayViewModel
import kotlinx.serialization.Serializable

@Serializable
data class EditCosplayElementScreen(val elementId: Int)

@Composable
fun EditCosplayElementScreen(
    elementId: Int, navController: NavController, cosplayViewModel: CosplayViewModel = viewModel()
) {
    val context = LocalContext.current
    val element by cosplayViewModel.getElementById(elementId).collectAsState(initial = null)

    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var ready by remember { mutableStateOf(false) }
    var bought by remember { mutableStateOf(false) }
    var photoPath by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

// Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val fileName = "cosplay_element_${System.currentTimeMillis()}.jpg"
                val savedPath = copyUriToInternalStorage(context, uri, fileName)
                photoPath = savedPath
            } catch (e: Exception) {
                error = "Failed to save image: ${e.localizedMessage}"
            }
        }
    }

// Load element data
    LaunchedEffect(element) {
        element?.let {
            name = it.name
            cost = it.cost?.toString() ?: ""
            ready = it.ready
            bought = it.bought
            photoPath = it.photoPath ?: ""
            notes = it.notes ?: ""
        }
    }

// Scrollable content so Save button is always visible
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // reduced horizontal padding
            .padding(top = 12.dp),       // reduced top padding
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image section with less padding
        Text(
            text = "Element Image",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Box(
            modifier = Modifier
                .size(80.dp) // smaller image size
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (photoPath.isNotEmpty()) {
                AsyncImage(
                    model = photoPath,
                    contentDescription = "Element image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Pick image",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        // Inputs with more rounded corners
        Text(
            text = "Basic Information",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp), // more rounded
            singleLine = true
        )

        OutlinedTextField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp), // more rounded
            singleLine = true
        )

        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = ready, onCheckedChange = { ready = it })
                Text("Ready")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = bought, onCheckedChange = { bought = it })
                Text("Bought")
            }
        }

        Text(
            text = "Notes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(20.dp), // more rounded
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save button always visible
        Button(
            onClick = {
                cosplayViewModel.updateElement(
                    id = elementId,
                    name = name,
                    cost = cost.toDoubleOrNull(),
                    ready = ready,
                    bought = bought,
                    photoPath = photoPath,
                    notes = notes
                )
                navController.popBackStack()
            }, modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // consistent button height
            shape = RoundedCornerShape(20.dp) // more rounded
        ) {
            Text("Save", style = MaterialTheme.typography.titleMedium)
        }
    }
}