package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
        modifier =
            modifier
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
        Text(
            text = event.eventName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = bodyColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // The venue is the only part that may run long, so it is the only part allowed to
            // shrink: the date stays whole and the chip keeps the right edge.
            EventListItemMeta(
                icon = Icons.Default.Place,
                text = event.eventLocation,
                color = bodyColor,
                modifier = Modifier.weight(1f, fill = false),
            )
            EventListItemMeta(
                icon = Icons.Default.Event,
                text = convertDateToString(event.eventDate),
                color = bodyColor,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bottom corner like a cosplay's status, but labelled where that one is not: every
            // type draws the same LocalActivity icon, so the word is what separates Expo from
            // Contest.
            MyStatusChip(
                label = event.eventType.displayName,
                icon = Icons.Default.LocalActivity,
                active = !isPast,
            )
        }
    }
}

@Composable
private fun EventListItemMeta(
    icon: ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
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
