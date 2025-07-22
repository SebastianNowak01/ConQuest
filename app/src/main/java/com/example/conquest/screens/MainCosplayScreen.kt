package com.example.conquest.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

@Serializable
data class MainCosplayScreen(
    val uid: Int
)

@Composable
fun MainCosplayScreen(
    navBackStackEntry: NavBackStackEntry,
    navController: NavController
) {
    CosplayTabs(navBackStackEntry, navController)
}

data class TabIcon(
    val imageVector: ImageVector, val contentDescription: String
)

@Composable
fun CosplayTabs(navBackStackEntry: NavBackStackEntry, navController: NavController) {
    val tabIcons = listOf(
        TabIcon(Icons.Filled.TheaterComedy, "Cosplay Elements"),
        TabIcon(Icons.AutoMirrored.Filled.List, "Tasks"),
        TabIcon(Icons.Filled.Image, "Reference Images")
    )
    val pagerState = rememberPagerState { tabIcons.size }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedTab) {
        if (pagerState.currentPage != selectedTab) {
            pagerState.animateScrollToPage(selectedTab)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage, modifier = Modifier.fillMaxWidth()
        ) {
            tabIcons.forEachIndexed { index, icon ->
                Tab(icon = {
                    Icon(
                        imageVector = icon.imageVector, contentDescription = icon.contentDescription
                    )
                }, selected = pagerState.currentPage == index, onClick = { selectedTab = index })
            }
        }

        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> CosplayElementsTab(navController, navBackStackEntry)
                1 -> TasksTab()
                2 -> ReferenceImagesTab(navBackStackEntry)
            }
        }
    }
}
