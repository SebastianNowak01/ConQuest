package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
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

@Composable
fun MyCosplayRow(cosplay: Cosplay) {
    val status = CosplayStatus.fromEntity(cosplay)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyImageBox(
            photoPath = cosplay.cosplayPhotoPath.orEmpty(),
            clickable = false,
            onClick = {},
            config =
                MyImageBoxConfig(
                    size = UIConsts.imageSizeM,
                    contentDescription = cosplay.name,
                    emptyContentDescription = "Cosplay photo",
                ),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(UIConsts.paddingXS),
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
                    text = "${cosplay.overallPercentage}%",
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

                Spacer(modifier = Modifier.weight(1f))

                // Bottom corner, icon only, the way a task states Done and an element Ready. As a
                // labelled chip beside the name it took the width the character name needed, and
                // the three statuses are different enough shapes that the label earns nothing.
                MyStatusChip(
                    label = status.label,
                    icon = status.icon,
                    active = true,
                    showLabel = false,
                )
            }
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
        Icon(
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
