package com.example.conquest.components

import MainNavigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

/**
 * routes is a list of all the routes in the navigation drawer.
 */
val routes = listOf(
    MainScreen,
    DetailScreen(name = null, age = 0)
)

/**
 * NavigationDrawerMobile composable is used to display the navigation drawer on a mobile device.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Drawer() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
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
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                unselectedContainerColor = MaterialTheme.colorScheme.background,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                    }
                }
            },
            drawerState = drawerState
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                            navigationIconContentColor = MaterialTheme.colorScheme.primary
                        ),
                        title = {
                            // Place the action button in the title area, which positions it right after the navigation icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically                            ) {
                                IconButton(onClick = {
                                    navController.navigate(NewCosplayScreen)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu, // Change icon as needed
                                        contentDescription = "Action Button"
                                    )
                                }
                                TextField(
                                    value = "",
                                    onValueChange = { /* Handle search input */ },
                                    placeholder = { Text("Search") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 16.dp),
                                    shape = RoundedCornerShape(24.dp), // Add rounded corners
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                                        disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        errorIndicatorColor = Color.Transparent
                                    )
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            // Three action buttons
                            IconButton(onClick = { /* First button action */ }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                            IconButton(onClick = { /* Second button action */ }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorite"
                                )
                            }
                            IconButton(onClick = { /* Third button action */ }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    )
                }
            )
            {
                MainNavigation(navController)
            }
        }
    }
}
