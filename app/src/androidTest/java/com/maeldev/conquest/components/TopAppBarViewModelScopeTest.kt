package com.maeldev.conquest.components

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.screens.MainScreen
import com.maeldev.conquest.viewmodel.CosplayViewModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The app's top app bar is composed in the Scaffold's topBar slot, which is outside the NavHost.
 * A bare viewModel() call there resolves against the Activity's ViewModelStore, while the screen
 * inside the NavHost resolves against its NavBackStackEntry — two different instances of the same
 * ViewModel, so every filter and sort the bar wrote went to an object the list never read.
 *
 * This reproduces that arrangement and pins the fix: the bar must scope its ViewModel to the
 * destination it belongs to.
 */
@RunWith(AndroidJUnit4::class)
class TopAppBarViewModelScopeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun topBarAndScreenShareOneViewModel() {
        var fromTopBar: CosplayViewModel? = null
        var fromScreen: CosplayViewModel? = null

        composeRule.setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            Scaffold(
                topBar = {
                    val entry = navBackStackEntry
                    if (entry != null) {
                        fromTopBar =
                            viewModel(
                                viewModelStoreOwner = entry,
                                factory = AppViewModelProvider.Factory,
                            )
                    }
                },
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = MainScreen,
                ) {
                    composable<MainScreen> {
                        fromScreen = viewModel(factory = AppViewModelProvider.Factory)
                        Text(text = "screen", modifier = androidx.compose.ui.Modifier)
                    }
                }
                padding.calculateTopPadding()
            }
        }

        composeRule.waitForIdle()

        assertNotNull("the top bar never resolved a ViewModel", fromTopBar)
        assertNotNull("the screen never resolved a ViewModel", fromScreen)
        assertSame(
            "the top bar and the screen must share one CosplayViewModel, or the filter and sort " +
                "the bar writes are invisible to the list",
            fromScreen,
            fromTopBar,
        )
    }
}
