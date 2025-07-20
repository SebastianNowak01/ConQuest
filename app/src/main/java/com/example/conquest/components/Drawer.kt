package com.example.conquest.components

import MainNavigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.conquest.screens.MainScreen
import com.example.conquest.screens.NewCosplayScreen
import com.example.conquest.screens.SettingsScreenParams
import kotlinx.coroutines.launch


val routes = listOf(
    MainScreen, SettingsScreenParams
)

val noDrawerRoutes = listOf(
    "com.example.conquest.screens.NewCosplayScreen"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer(
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (currentRoute !in noDrawerRoutes) {
            ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.background,
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ConQuest",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        navigationItems.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                label = { Text(text = item.title) },
                                selected = index == selectedItemIndex,
                                onClick = {
                                    navController.navigate(routes[index])
                                    selectedItemIndex = index
                                    scope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                colors = navigationDrawerItemColorsObject()
                            )
                        }
                    }
                }, drawerState = drawerState
            ) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    topBar = {
                        TopAppBar(colors = topAppBarColorsObject(), title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SearchBar(
                                    value = searchQuery, onValueChange = { searchQuery = it })
                            }
                        }, navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu, contentDescription = "Menu"
                                )
                            }
                        }, actions = {
                            MyIcon({}, Icons.Default.Search, "Filter")
                            MyIcon({}, Icons.Default.AccountCircle, "Sort by")
                            MyIcon({}, Icons.Default.KeyboardArrowDown, "Order by")
                        })
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(NewCosplayScreen)
                            },
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add new cosplay")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        HorizontalDivider(thickness = 1.dp)
                        MainNavigation(navController, searchQuery)
                    }
                }
            }
        } else {
            MainNavigation(navController, "")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBarColorsObject(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.primary,
        navigationIconContentColor = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBarTextFieldColorsObject(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.tertiary,
        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
        disabledContainerColor = MaterialTheme.colorScheme.tertiary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun navigationDrawerItemColorsObject(): NavigationDrawerItemColors {
    return NavigationDrawerItemDefaults.colors(
        selectedContainerColor = MaterialTheme.colorScheme.secondary,
        unselectedContainerColor = MaterialTheme.colorScheme.background,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedTextColor = MaterialTheme.colorScheme.secondary,
    )
}