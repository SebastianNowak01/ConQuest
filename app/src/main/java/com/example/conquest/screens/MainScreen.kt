package com.example.conquest.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import kotlinx.serialization.Serializable
import androidx.compose.ui.text.font.FontWeight
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyLazyColumn
import com.example.conquest.components.MyOuterBox
import com.example.conquest.screens.cosplay.MainCosplayScreen
import com.example.conquest.screens.cosplay.NewCosplay
import com.example.conquest.ui.theme.UIConsts

@Serializable
object MainScreen

@Composable
fun MainScreen(navController: NavController, searchQuery: String) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val cosplays by cosplayViewModel.allCosplays.collectAsState()

    val filteredCosplays = remember(cosplays, searchQuery) {
        if (searchQuery.isBlank()) cosplays
        else cosplays.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    MyOuterBox {
        if (selectionMode) {
            MyDeleteFab(onClick = {
                cosplayViewModel.deleteCosplaysByIds(selectedIds)
                selectionMode = false
                selectedIds = emptySet()
            })
        }

        MyLazyColumn(
            items = filteredCosplays,
            key = { it.uid },
            isSelected = { selectedIds.contains(it.uid) },
            onClick = { cosplay ->
                if (!selectionMode) {
                    navController.navigate(MainCosplayScreen(cosplay.uid))
                    return@MyLazyColumn
                }
                val id = cosplay.uid
                selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                if (selectedIds.isEmpty()) selectionMode = false
            },
            onLongClick = { cosplay ->
                selectionMode = true
                selectedIds = selectedIds + cosplay.uid
            },
        ) { cosplay ->
            Row {
                MyImageBox(
                    photoPath = cosplay.cosplayPhotoPath ?: "",
                    contentDescription = cosplay.name,
                    size = UIConsts.imageSizeS,
                    clickable = false,
                    onClick = {},
                    emptyContentDescription = "Cosplay photo",
                )
                Spacer(modifier = Modifier.width(UIConsts.paddingS))
                Column {
                    Text(
                        text = cosplay.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = cosplay.series,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
        MyAddFab(navController, route = NewCosplay)
    }
}