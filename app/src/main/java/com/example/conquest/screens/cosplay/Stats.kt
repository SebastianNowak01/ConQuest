package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyOuterBox
import com.example.conquest.ui.theme.UIConsts
import kotlinx.serialization.Serializable

@Serializable
data class Stats(val cosplayId: Int)

@Composable
fun StatsScreen(
    cosplayId: Int,
    cosplayViewModel: CosplayViewModel = viewModel(),
) {
    val cosplay by cosplayViewModel.getCosplayById(cosplayId).collectAsState(initial = null)
    val progressPhotos by cosplayViewModel.progressPhotos.collectAsState()

    LaunchedEffect(cosplayId) {
        cosplayViewModel.setProgressCosplayId(cosplayId)
    }

    MyOuterBox {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(UIConsts.paddingM)
        ) {
            val currentCosplay = cosplay
            if (currentCosplay != null) {
                StatCard(
                    icon = Icons.Default.AttachMoney,
                    title = "Total Cost",
                    value = String.format("$%.2f", currentCosplay.totalSpend)
                )
                Spacer(modifier = Modifier.height(UIConsts.paddingM))
                StatCard(
                    icon = Icons.Default.Schedule,
                    title = "Total Days Worked",
                    value = currentCosplay.totalTimeDays.toString()
                )
                Spacer(modifier = Modifier.height(UIConsts.paddingM))
                StatCard(
                    icon = Icons.Default.Event,
                    title = "Events Worn On",
                    value = currentCosplay.eventsCount.toString()
                )
                Spacer(modifier = Modifier.height(UIConsts.paddingM))
                StatCard(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Progress Pictures",
                    value = progressPhotos.size.toString()
                )
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = UIConsts.paddingM)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
