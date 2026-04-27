package com.example.conquest.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.conquest.data.entity.Event
import com.example.conquest.data.entity.EventType
import com.example.conquest.ui.theme.UIConsts
import java.util.Date

@Composable
fun EventsFilters(
    selectedType: EventType?,
    selectedDate: Date?,
    onTypeSelected: (EventType?) -> Unit,
    onDateSelected: (Date?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = UIConsts.paddingM),
    ) {
        MyHeaderText(text = "Events")

        EventTypeDropdown(
            selectedType = selectedType,
            onTypeSelected = onTypeSelected,
            label = "Filter by Type",
            allowAllOption = true,
        )

        DatePickerFieldToModal(
            label = "Filter by Date",
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
        )

        if (selectedType != null || selectedDate != null) {
            TextButton(onClick = onClearFilters) {
                Text(text = "Clear Filters")
            }
        }
    }
}

@Composable
fun EventListItem(event: Event) {
    Text(
        text = event.eventName,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        text = event.eventLocation,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        text = "${event.eventType.displayName} - ${convertDateToString(event.eventDate)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
    )
    if (!event.description.isNullOrBlank()) {
        Text(
            text = event.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

