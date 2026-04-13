package com.example.conquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.conquest.components.MyTopAppBar
import com.example.conquest.components.Drawer
import com.example.conquest.components.MainNavigation
import com.example.conquest.components.getTopAppBarConfig
import com.example.conquest.components.noDrawerRoutes
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
            val cosplayViewModel: CosplayViewModel = viewModel()
            val cosplays by cosplayViewModel.allCosplays.collectAsState()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            var searchQuery by rememberSaveable { mutableStateOf("") }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isNoDrawerRoute = currentRoute in noDrawerRoutes
            val currentCosplayId = navBackStackEntry?.arguments?.getInt("uid")
            val currentCosplay = cosplays.firstOrNull { it.uid == currentCosplayId }

            val topBarConfig = getTopAppBarConfig(currentRoute, noDrawerRoutes)

            ConQuestTheme(darkTheme = darkTheme) {
                Drawer(
                    navController = navController,
                    drawerState = drawerState,
                ) {
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        topBar = {
                            MyTopAppBar(
                                config = topBarConfig,
                                searchQuery = searchQuery,
                                cosplayName = currentCosplay?.name ?: "",
                                cosplaySeries = currentCosplay?.series ?: "",
                                cosplayPhotoPath = currentCosplay?.cosplayPhotoPath ?: "",
                                onSearchQueryChange = { searchQuery = it },
                                onMenuClick = { scope.launch { drawerState.open() } })
                        }) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            if (!isNoDrawerRoute) {
                                HorizontalDivider(thickness = 1.dp)
                            }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.TopCenter
                            ) {
                                Box(
                                    modifier = Modifier.widthIn(max = 600.dp)
                                ) {
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
    }
}
