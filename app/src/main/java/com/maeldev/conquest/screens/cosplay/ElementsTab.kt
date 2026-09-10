package com.maeldev.conquest.screens.cosplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyAddFab
import com.maeldev.conquest.components.MyEmptyState
import com.maeldev.conquest.components.MyImageBox
import com.maeldev.conquest.components.MyImageBoxConfig
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyListItemActions
import com.maeldev.conquest.components.MyListStyle
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySelectionCountLabel
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.components.MyStatusChip
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.theme.UIConsts
import com.maeldev.conquest.viewmodel.ElementViewModel

@Composable
fun ElementsTab(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
) {
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
            actions =
                MyListItemActions(
                    isSelected = { selection.isSelected(it.id) },
                    onClick = { element ->
                        if (!selection.isActive) {
                            navController.navigate(EditElement(element.id))
                            return@MyListItemActions
                        }
                        selection.toggle(element.id)
                    },
                    onLongClick = { element -> selection.select(element.id) },
                ),
            style = MyListStyle(cardContentPadding = UIConsts.spacingS),
        ) { element ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
            ) {
                MyImageBox(
                    photoPath = element.photoPath.orEmpty(),
                    clickable = false,
                    onClick = {},
                    config =
                        MyImageBoxConfig(
                            size = UIConsts.imageSizeS,
                            contentDescription = "Element image",
                        ),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(UIConsts.paddingXS),
                ) {
                    Text(
                        text = element.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (element.cost != null) {
                        Text(
                            text = "Cost: $${element.cost}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }

                // IntrinsicSize.Max sizes the column to the wider chip ("Bought"), and
                // fillMaxWidth then stretches the narrower one to match it.
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(UIConsts.paddingS),
                ) {
                    MyStatusChip(
                        label = "Ready",
                        icon = Icons.Default.CheckCircle,
                        active = element.ready,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MyStatusChip(
                        label = "Bought",
                        icon = Icons.Default.ShoppingCart,
                        active = element.bought,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        if (elements.isEmpty()) {
            MyEmptyState(
                icon = Icons.Default.TheaterComedy,
                title = "No elements yet",
                hint = "Wigs, props, each piece of the outfit — track them and what they cost.",
                modifier = Modifier.align(Alignment.Center),
            )
        }

        MySelectionCountLabel(selection = selection, itemLabelSingular = "element")

        MyAddFab(navController, route = NewElement(cosplayId))
    }
}
