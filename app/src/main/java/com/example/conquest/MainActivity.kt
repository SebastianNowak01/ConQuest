// Kotlin
// File: app/src/main/java/com/example/conquest/MainActivity.kt
package com.example.conquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.conquest.components.Drawer
import com.example.conquest.components.MainNavigation
import com.example.conquest.components.noDrawerRoutes
import com.example.conquest.components.topAppBarColorsObject
import com.example.conquest.screens.rememberThemePreference
import com.example.conquest.ui.theme.ConQuestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themePref by rememberThemePreference(context)
            val darkTheme = when (themePref) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            var searchQuery by rememberSaveable { mutableStateOf("") }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isNoDrawerRoute = currentRoute in noDrawerRoutes
            val isSettings = currentRoute == "com.example.conquest.screens.SettingsScreenParams"

            ConQuestTheme(darkTheme = darkTheme) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    topBar = {
                        if (!isNoDrawerRoute) {
                            if (isSettings) {
                                TopAppBar(
                                    colors = topAppBarColorsObject(),
                                    title = { Text("Settings screen") },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(
                                                imageVector = Icons.Default.Menu,
                                                contentDescription = "Menu"
                                            )
                                        }
                                    })
                            } else {
                                TopAppBar(colors = topAppBarColorsObject(), title = {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        com.example.conquest.components.SearchBar(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it })
                                    }
                                }, navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu"
                                        )
                                    }
                                }, actions = {
                                    com.example.conquest.components.MyIcon(
                                        {}, Icons.Default.Search, "Filter"
                                    )
                                    com.example.conquest.components.MyIcon(
                                        {}, Icons.Default.AccountCircle, "Sort by"
                                    )
                                    com.example.conquest.components.MyIcon(
                                        {}, Icons.Default.KeyboardArrowDown, "Order by"
                                    )
                                })
                            }
                        }
                    }) { padding ->
                    Drawer(
                        navController = navController,
                        drawerState = drawerState,
                        currentRoute = currentRoute
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            if (!isNoDrawerRoute) {
                                HorizontalDivider(thickness = 1.dp)
                            }
                            MainNavigation(
                                navController = navController,
                                searchQuery = if (isNoDrawerRoute) "" else searchQuery
                            )
                        }
                    }
                }
            }
        }
    }
}