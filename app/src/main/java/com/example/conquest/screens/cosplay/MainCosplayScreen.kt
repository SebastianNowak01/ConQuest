package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun CosplayTabs(
    navBackStackEntry: NavBackStackEntry, navController: NavController, initialTab: Int
) {
    val tabIcons = listOf(
        TabIcon(Icons.Filled.TheaterComedy, "Cosplay Elements"),
        TabIcon(Icons.AutoMirrored.Filled.List, "Tasks"),
        TabIcon(Icons.Filled.Image, "Reference Images")
    )

    val pagerState = rememberPagerState(initialPage = initialTab) { tabIcons.size }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabIcons.forEachIndexed { index, icon ->
                Tab(
                    icon = { Icon(icon.imageVector, icon.contentDescription) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    })
            }
        }

        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> ElementsTab(navController, navBackStackEntry)
                1 -> CosplayTasksTab(navController, navBackStackEntry)
                2 -> PhotosTab(navBackStackEntry, navController)
            }
        }
    }
}
