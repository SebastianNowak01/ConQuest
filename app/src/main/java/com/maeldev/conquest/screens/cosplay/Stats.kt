package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maeldev.conquest.viewmodel.CosplayViewModel
import com.maeldev.conquest.viewmodel.ProgressPhotoViewModel
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.theme.UIConsts
import kotlinx.serialization.Serializable

@Serializable
data class Stats(val cosplayId: Int)

@Composable
fun StatsScreen(
    cosplayId: Int,
    cosplayViewModel: CosplayViewModel = viewModel(factory = AppViewModelProvider.Factory),
    progressPhotoViewModel: ProgressPhotoViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val cosplay by cosplayViewModel.getCosplayById(cosplayId).collectAsState(initial = null)
    val progressPhotos by progressPhotoViewModel.progressPhotos.collectAsState()

    LaunchedEffect(cosplayId) {
        progressPhotoViewModel.setProgressCosplayId(cosplayId)
    }

    MyOuterBox {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(UIConsts.paddingM),
            verticalArrangement = Arrangement.spacedBy(UIConsts.paddingM),
        ) {
            val currentCosplay = cosplay
            if (currentCosplay != null) {
                StatCard(
                    icon = Icons.Default.AttachMoney,
                    title = "Total Cost",
                    value = String.format(
                        java.util.Locale.getDefault(),
                        "$%.2f",
                        currentCosplay.totalSpend,
                    ),
                )
                StatCard(
                    icon = Icons.Default.Schedule,
                    title = "Total Days Worked",
                    value = currentCosplay.totalTimeDays.toString(),
                )
                StatCard(
                    icon = Icons.Default.Event,
                    title = "Events Worn On",
                    value = currentCosplay.eventsCount.toString(),
                )
                StatCard(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Progress Pictures",
                    value = progressPhotos.size.toString(),
                )
            }
        }
    }
}

/**
 * One statistic. The number is the point of the card, so it leads and the label sits under it —
 * these used to be the same weight, with the label on top. The card matches the bordered,
 * elevated style every other card in the app uses rather than the Material default.
 */
@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
) {
    val shape = RoundedCornerShape(UIConsts.cornerRadiusL)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(UIConsts.strokeThin, MaterialTheme.colorScheme.outline, shape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = UIConsts.elevationS),
        shape = shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UIConsts.paddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                // Not the secondary green: as a bare tint rather than a filled container it is
                // too dark against this background to read.
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = UIConsts.paddingM)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
