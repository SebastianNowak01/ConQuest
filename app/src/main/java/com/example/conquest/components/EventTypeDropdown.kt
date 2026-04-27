package com.example.conquest.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.conquest.data.entity.EventType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTypeDropdown(
    selectedType: EventType?,
    onTypeSelected: (EventType?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Type of Event*",
    allowAllOption: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedType?.displayName ?: "All",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,
            ),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (allowAllOption) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        onTypeSelected(null)
                        expanded = false
                    },
                )
            }
            EventType.entries.forEach { eventType ->
                DropdownMenuItem(
                    text = { Text(eventType.displayName) },
                    onClick = {
                        onTypeSelected(eventType)
                        expanded = false
                    },
                )
            }
        }
    }
}

val EventType.displayName: String
    get() = when (this) {
        EventType.EXPO -> "Expo"
        EventType.CONVENTION -> "Convention"
        EventType.CONTEST -> "Contest"
        EventType.MEETING -> "Meeting"
        EventType.PARTY -> "Party"
    }



