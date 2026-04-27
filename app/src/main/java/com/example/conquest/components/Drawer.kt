package com.example.conquest.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.conquest.screens.MainScreen
import com.example.conquest.screens.SettingsScreenParams
import com.example.conquest.screens.cosplay.Events
import com.example.conquest.screens.cosplay.MainCosplayScreen
import com.example.conquest.screens.cosplay.Progress
import com.example.conquest.screens.cosplay.Stats
import com.example.conquest.ui.theme.UIConsts
import kotlinx.coroutines.launch

val routes = listOf(
    MainScreen,
    SettingsScreenParams,
    Events,
)

val noDrawerRoutes = listOf(
    "com.example.conquest.screens.cosplay.NewCosplay",
    "com.example.conquest.screens.cosplay.NewEvent",
    "com.example.conquest.screens.cosplay.EditEvent/{eventId}",
    "com.example.conquest.screens.cosplay.EditProgressPhoto/{photoId}/{cosplayId}",
    "com.example.conquest.screens.cosplay.NewElement/{cosplayId}",
    "com.example.conquest.screens.cosplay.NewTask/{cosplayId}",
    "com.example.conquest.screens.cosplay.EditElement/{elementId}",
    "com.example.conquest.screens.cosplay.EditTask/{taskId}",
    "com.example.conquest.screens.cosplay.EditPhoto/{photoId}",
    "com.example.conquest.screens.cosplay.EditCosplay/{cosplayId}",
)

@Composable
fun Drawer(
    navController: NavHostController, drawerState: DrawerState, content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val arguments = navBackStackEntry?.arguments
    val currentCosplayId = if (arguments?.containsKey("uid") == true) {
        arguments.getInt("uid")
    } else if (arguments?.containsKey("cosplayId") == true) {
        arguments.getInt("cosplayId")
    } else {
        null
    }
    val isInCosplayTabs = currentRoute?.startsWith("com.example.conquest.screens.cosplay.MainCosplayScreen") == true ||
            currentRoute?.startsWith("com.example.conquest.screens.cosplay.Progress") == true ||
            currentRoute?.startsWith("com.example.conquest.screens.cosplay.Stats") == true

    val drawerItems = if (isInCosplayTabs && currentCosplayId != null) {
        navigationItems + progressNavigationItem + statsNavigationItem
    } else {
        navigationItems
    }
    val drawerRoutes = if (isInCosplayTabs && currentCosplayId != null) {
        routes + Progress(currentCosplayId) + Stats(currentCosplayId)
    } else {
        routes
    }

    val selectedItemIndex = drawerRoutes.indexOfFirst { route ->
        currentRoute?.startsWith(route::class.qualifiedName ?: "") == true
    }.takeIf { it >= 0 } ?: 0

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                Spacer(modifier = Modifier.height(UIConsts.paddingM))
                Text(
                    text = "ConQuest",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        horizontal = UIConsts.paddingL,
                        vertical = UIConsts.paddingS,
                    )
                )
                HorizontalDivider(
                    thickness = UIConsts.strokeThin,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = UIConsts.paddingS)
                )
                drawerItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            navController.navigate(drawerRoutes[index])
                            scope.launch { drawerState.close() }
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
        }) {
        content()
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
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        errorContainerColor = MaterialTheme.colorScheme.background,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )
}

@Composable
fun navigationDrawerItemColorsObject(): NavigationDrawerItemColors {
    return NavigationDrawerItemDefaults.colors(
        selectedContainerColor = MaterialTheme.colorScheme.secondary,
        unselectedContainerColor = MaterialTheme.colorScheme.background,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedTextColor = MaterialTheme.colorScheme.secondary,
    )
}