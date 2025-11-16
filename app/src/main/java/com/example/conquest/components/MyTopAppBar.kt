package com.example.conquest.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

sealed class MyTopAppBar {
    data object Default : MyTopAppBar()
    data object Settings : MyTopAppBar()
    data class Cosplay(val title: String = "Cosplay Details") : MyTopAppBar()
    data object None : MyTopAppBar()
}

@Composable
fun getTopAppBarConfig(route: String?, noDrawerRoutes: List<String>): MyTopAppBar {
    return when {
        route == null -> MyTopAppBar.None
        route in noDrawerRoutes -> MyTopAppBar.None
        route == "com.example.conquest.screens.SettingsScreenParams" -> MyTopAppBar.Settings
        route.startsWith("com.example.conquest.screens.cosplay.") -> MyTopAppBar.Cosplay()
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
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CosplayTopAppBar(
    title: String,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        colors = topAppBarColorsObject(),
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        colors = topAppBarColorsObject(),
        title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            MyIcon(
                onClick = {},
                imageVector = Icons.Default.Search,
                contentDescription = "Filter"
            )
            MyIcon(
                onClick = {},
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Sort by"
            )
            MyIcon(
                onClick = {},
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Order by"
            )
        }
    )
}

@Composable
fun MyTopAppBar(
    config: MyTopAppBar,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    when (config) {
        MyTopAppBar.None -> { /* No TopAppBar */ }
        MyTopAppBar.Settings -> SettingsTopAppBar(onMenuClick)
        is MyTopAppBar.Cosplay -> CosplayTopAppBar(config.title, onMenuClick)
        MyTopAppBar.Default -> DefaultTopAppBar(searchQuery, onSearchQueryChange, onMenuClick)
    }
}
