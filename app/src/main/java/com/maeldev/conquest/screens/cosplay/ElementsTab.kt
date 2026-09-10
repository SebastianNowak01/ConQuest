package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.maeldev.conquest.viewmodel.ElementViewModel
import com.maeldev.conquest.components.MyAddFab
import com.maeldev.conquest.components.MyImageBox
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.theme.UIConsts

@Composable
fun ElementsTab(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val mainArgs = navBackStackEntry.toRoute<MainCosplayScreen>()
    val cosplayId = mainArgs.uid

    val elementViewModel: ElementViewModel = viewModel(factory = AppViewModelProvider.Factory)

    LaunchedEffect(cosplayId) {
        elementViewModel.setElementCosplayId(cosplayId)
    }

    val elements by elementViewModel.elements.collectAsState()

    val selection = rememberSelectionState(items = elements, id = { it.id })

    MyOuterBox {
        if (selection.isActive) {
            MySelectionModeFabs(
                selection = selection,
                itemLabelSingular = "element",
                itemLabelPlural = "elements",
                onDeleteSelection = { ids -> elementViewModel.deleteElementsByIds(ids) },
            )
        }

        MyLazyColumn(
            items = elements,
            key = { it.id },
            isSelected = { selection.isSelected(it.id) },
            onClick = { element ->
                if (!selection.isActive) {
                    navController.navigate(EditElement(element.id))
                    return@MyLazyColumn
                }
                selection.toggle(element.id)
            },
            onLongClick = { element -> selection.select(element.id) },
            cardContentPadding = UIConsts.spacingS,
        ) { element ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS)
            ) {
                MyImageBox(
                    photoPath = element.photoPath.orEmpty(),
                    contentDescription = "Element image",
                    size = UIConsts.imageSizeS,
                    clickable = false,
                    onClick = {},
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(UIConsts.paddingXS)
                ) {
                    Text(
                        text = element.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (element.cost != null) {
                        Text(
                            text = "Cost: $${element.cost}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingXS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (element.ready) {
                        Text(
                            "Ready",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (element.bought) {
                        Text(
                            "Bought",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        MyAddFab(navController, route = NewElement(cosplayId))
    }
}
