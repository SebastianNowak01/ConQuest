package com.example.conquest.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.example.conquest.CosplayViewModel
import com.example.conquest.data.entity.Cosplay
import com.example.conquest.screens.cosplay.EditCosplay
import com.example.conquest.ui.theme.UIConsts
import kotlinx.coroutines.flow.flowOf

sealed class MyTopAppBar {
    data object Default : MyTopAppBar()
    data object Settings : MyTopAppBar()
    data object Events : MyTopAppBar()
    data object Progress : MyTopAppBar()
    data object Cosplay : MyTopAppBar()
    data object None : MyTopAppBar()
}

@Composable
fun getTopAppBarConfig(route: String?, noDrawerRoutes: List<String>): MyTopAppBar {
    return when {
        route == null -> MyTopAppBar.None
        route in noDrawerRoutes -> MyTopAppBar.None
        route == "com.example.conquest.screens.SettingsScreenParams" -> MyTopAppBar.Settings
        route == "com.example.conquest.screens.cosplay.Events" -> MyTopAppBar.Events
        route.startsWith("com.example.conquest.screens.cosplay.Progress") -> MyTopAppBar.Progress
        route.startsWith("com.example.conquest.screens.cosplay.") -> MyTopAppBar.Cosplay
        else -> MyTopAppBar.Default
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(onMenuClick: () -> Unit) {
    TopAppBar(
        colors = topAppBarColorsObject(),
        title = { Text("Settings screen") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = "Menu"
                )
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsTopAppBar(onMenuClick: () -> Unit) {
    TopAppBar(
        colors = topAppBarColorsObject(),
        title = { Text("Events") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressTopAppBar(onMenuClick: () -> Unit) {
    TopAppBar(
        colors = topAppBarColorsObject(),
        title = { Text("Progress") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CosplayTopAppBar(
    navBackStackEntry: NavBackStackEntry?,
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val cosplayId = navBackStackEntry?.arguments?.getInt("uid")
    val cosplayFlow = remember(cosplayId) {
        if (cosplayId == null) {
            flowOf<Cosplay?>(null)
        } else {
            cosplayViewModel.getCosplayById(cosplayId)
        }
    }
    val cosplay by cosplayFlow.collectAsState(initial = null)

    TopAppBar(
        colors = topAppBarColorsObject(),
        title = {
            val loadedCosplay = cosplay
            if (loadedCosplay != null) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { navController.navigate(EditCosplay(loadedCosplay.uid)) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MyImageBox(
                        photoPath = loadedCosplay.cosplayPhotoPath ?: "",
                        size = UIConsts.imageSizeS,
                        clickable = false,
                        onClick = {},
                        contentDescription = loadedCosplay.name,
                        emptyContentDescription = "Cosplay photo",
                    )
                    Spacer(modifier = Modifier.width(UIConsts.paddingS))
                    Column {
                        Text(
                            text = loadedCosplay.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = loadedCosplay.series,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = "Menu"
                )
            }
        })
}

@Composable
fun MyTopAppBar(
    config: MyTopAppBar,
    searchQuery: String,
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry?,
    onSearchQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit,
) {
    when (config) {
        MyTopAppBar.None -> {/* No Top App bar*/}
        MyTopAppBar.Settings -> SettingsTopAppBar(onMenuClick)
        MyTopAppBar.Events -> EventsTopAppBar(onMenuClick)
        MyTopAppBar.Progress -> ProgressTopAppBar(onMenuClick)
        is MyTopAppBar.Cosplay -> CosplayTopAppBar(
            navBackStackEntry = navBackStackEntry,
            navController = navController,
            onMenuClick = onMenuClick,
        )
        MyTopAppBar.Default -> MainScreenTopAppBar(
            searchQuery = searchQuery,
            navBackStackEntry = navBackStackEntry,
            onSearchQueryChange = onSearchQueryChange,
            onMenuClick = onMenuClick,
        )
    }
}
