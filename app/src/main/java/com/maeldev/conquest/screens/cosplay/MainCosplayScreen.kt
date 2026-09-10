package com.maeldev.conquest.screens.cosplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch

@Serializable
data class MainCosplayScreen(
    val uid: Int, val initialTab: Int = 0
)

@Composable
fun MainCosplayScreen(
    navBackStackEntry: NavBackStackEntry, navController: NavController
) {
    val args = navBackStackEntry.toRoute<MainCosplayScreen>()
    CosplayTabs(
        navBackStackEntry = navBackStackEntry,
        navController = navController,
        initialTab = args.initialTab
    )
}

data class TabIcon(
    val imageVector: ImageVector, val contentDescription: String
)

private val tabs = listOf(
    TabIcon(Icons.Filled.TheaterComedy, "Elements"),
    TabIcon(Icons.AutoMirrored.Filled.List, "Tasks"),
    TabIcon(Icons.Filled.Image, "Photos"),
)

@Composable
fun CosplayTabs(
    navBackStackEntry: NavBackStackEntry, navController: NavController, initialTab: Int
) {
    val tabIcons = tabs

    val handle = navBackStackEntry.savedStateHandle
    val savedPage = handle.get<Int>("tab") ?: initialTab

    val pagerState = rememberPagerState(
        initialPage = savedPage.coerceIn(0, tabIcons.lastIndex), pageCount = { tabIcons.size })

    LaunchedEffect(pagerState.currentPage) {
        handle["tab"] = pagerState.currentPage
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabIcons.forEachIndexed { index, icon ->
                Tab(
                    icon = { Icon(icon.imageVector, contentDescription = null) },
                    // The theatre-masks glyph for "Elements" is not self-explanatory, so the
                    // tabs carry their names rather than relying on the icons alone.
                    text = { Text(text = icon.contentDescription) },
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } })
            }
        }
        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> ElementsTab(navController, navBackStackEntry)
                1 -> TasksTab(navController, navBackStackEntry)
                2 -> PhotosTab(navBackStackEntry, navController)
            }
        }
    }
}
