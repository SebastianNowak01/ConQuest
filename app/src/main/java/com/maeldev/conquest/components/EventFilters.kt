package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.maeldev.conquest.data.entity.Event
import com.maeldev.conquest.data.entity.EventType
import com.maeldev.conquest.data.isBeforeToday
import com.maeldev.conquest.theme.UIConsts
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
            .padding(horizontal = UIConsts.screenHorizontalPadding),
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

/**
 * An event as it appears in the list.
 *
 * This was four stacked lines all in the same colour, one of them the string "Expo - 14/03/2026",
 * with nothing separating the name from the details and no sign of whether an event had already
 * happened. The name now leads, the type is a chip, location and date carry icons, and an event
 * whose date has passed is dimmed.
 */
@Composable
fun EventListItem(event: Event) {
    val isPast = event.eventDate.isBeforeToday()
    val bodyColor =
        if (isPast) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.primary
        }

    Column(verticalArrangement = Arrangement.spacedBy(UIConsts.paddingXS)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingS),
        ) {
            Text(
                text = event.eventName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = bodyColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            MyStatusChip(
                label = event.eventType.displayName,
                icon = Icons.Default.LocalActivity,
                active = !isPast,
            )
        }

        EventListItemMeta(
            icon = Icons.Default.Place,
            text = event.eventLocation,
            color = bodyColor,
        )
        EventListItemMeta(
            icon = Icons.Default.Event,
            text = convertDateToString(event.eventDate),
            color = bodyColor,
        )

        if (!event.description.isNullOrBlank()) {
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EventListItemMeta(
    icon: ImageVector,
    text: String,
    color: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingXS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(UIConsts.chipIconSize),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

