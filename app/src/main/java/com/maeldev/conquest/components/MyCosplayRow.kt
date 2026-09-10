package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.maeldev.conquest.data.classes.CosplayStatus
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.theme.DarkGreen
import com.maeldev.conquest.theme.LightGreen
import com.maeldev.conquest.theme.UIConsts

private val CosplayStatus.icon: ImageVector
    get() =
        when (this) {
            CosplayStatus.Planned -> Icons.Default.Schedule
            CosplayStatus.InProgress -> Icons.Default.PlayArrow
            CosplayStatus.Done -> Icons.Default.CheckCircle
        }

/**
 * A cosplay as it appears in the main list.
 *
 * This used to be a photo, a name and a series and nothing else, while eight of the ten sort
 * options order the list by fields it never showed — so sorting by budget, tasks or due date
 * looked like it did nothing at all. Status, progress, due date and task count are now on the
 * row, which is also what makes the status vocabulary visible outside the edit screen.
 */
@Composable
fun MyCosplayRow(cosplay: Cosplay) {
    val status = CosplayStatus.fromEntity(cosplay)

    Column(verticalArrangement = Arrangement.spacedBy(UIConsts.paddingS)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MyImageBox(
                photoPath = cosplay.cosplayPhotoPath.orEmpty(),
                clickable = false,
                onClick = {},
                config =
                    MyImageBoxConfig(
                        size = UIConsts.imageSizeS,
                        contentDescription = cosplay.name,
                        emptyContentDescription = "Cosplay photo",
                    ),
            )

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = UIConsts.paddingS),
            ) {
                Text(
                    text = cosplay.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = cosplay.series,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            MyStatusChip(
                label = status.label,
                icon = status.icon,
                active = true,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LinearProgressIndicator(
                progress = { cosplay.overallPercentage / 100f },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(UIConsts.progressBarHeight)
                        .clip(RoundedCornerShape(UIConsts.progressBarHeight)),
                color = LightGreen,
                trackColor = DarkGreen,
                // Material 3 draws a dot in the indicator colour at the end of the track by
                // default, which reads as stray light green on a bar that is barely filled.
                drawStopIndicator = {},
            )
            Text(
                text = "${cosplay.overallPercentage}% done",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            cosplay.dueDate?.let { due ->
                MyCosplayMeta(
                    icon = Icons.Default.Event,
                    text = convertDateToString(due),
                )
            }
            MyCosplayMeta(
                icon = Icons.AutoMirrored.Filled.List,
                text = if (cosplay.tasksCount == 1) "1 task" else "${cosplay.tasksCount} tasks",
            )
        }
    }
}

@Composable
private fun MyCosplayMeta(
    icon: ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingXS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(UIConsts.chipIconSize),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
