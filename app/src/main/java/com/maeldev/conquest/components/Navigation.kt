package com.maeldev.conquest.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.maeldev.conquest.screens.MainScreen
import com.maeldev.conquest.screens.SettingsScreen
import com.maeldev.conquest.screens.SettingsScreenParams
import com.maeldev.conquest.screens.cosplay.EditCosplay
import com.maeldev.conquest.screens.cosplay.EditElement
import com.maeldev.conquest.screens.cosplay.EditEvent
import com.maeldev.conquest.screens.cosplay.EditPhoto
import com.maeldev.conquest.screens.cosplay.EditProgressPhoto
import com.maeldev.conquest.screens.cosplay.EditTask
import com.maeldev.conquest.screens.cosplay.Events
import com.maeldev.conquest.screens.cosplay.EventsScreen
import com.maeldev.conquest.screens.cosplay.MainCosplayScreen
import com.maeldev.conquest.screens.cosplay.NewCosplay
import com.maeldev.conquest.screens.cosplay.NewElement
import com.maeldev.conquest.screens.cosplay.NewEvent
import com.maeldev.conquest.screens.cosplay.NewTask
import com.maeldev.conquest.screens.cosplay.Progress
import com.maeldev.conquest.screens.cosplay.ProgressScreen
import com.maeldev.conquest.screens.cosplay.Stats
import com.maeldev.conquest.screens.cosplay.StatsScreen


@Composable
fun MainNavigation(
    navController: NavHostController,
    searchQuery: String,
) {
    val navController = navController
    NavHost(
        navController = navController,
        startDestination = MainScreen,
    ) {
        composable<MainScreen> {
            MainScreen(
                navController = navController,
                searchQuery = searchQuery,
            )
        }
        composable<SettingsScreenParams> {
            SettingsScreen()
        }
        composable<Events> {
            EventsScreen(
                navController = navController,
                searchQuery = searchQuery,
            )
        }
        composable<Progress> { backStackEntry ->
            val args = backStackEntry.toRoute<Progress>()
            ProgressScreen(
                navController = navController,
                cosplayId = args.cosplayId,
            )
        }
        composable<Stats> { backStackEntry ->
            val args = backStackEntry.toRoute<Stats>()
            StatsScreen(
                cosplayId = args.cosplayId,
            )
        }
        composable<NewEvent> {
            NewEvent(navController = navController)
        }
        composable<EditEvent> { backStackEntry ->
            val args = backStackEntry.toRoute<EditEvent>()
            EditEvent(
                eventId = args.eventId,
                navController = navController,
            )
        }
        composable<EditProgressPhoto> { backStackEntry ->
            val args = backStackEntry.toRoute<EditProgressPhoto>()
            EditProgressPhoto(
                photoId = args.photoId,
                cosplayId = args.cosplayId,
                navController = navController,
            )
        }
        composable<NewCosplay> {
            NewCosplay(navController)
        }
        composable<MainCosplayScreen> { backStackEntry ->
            MainCosplayScreen(
                backStackEntry,
                navController
            )
        }
        composable<NewElement> { backStackEntry ->
            val args = backStackEntry.toRoute<NewElement>()
            NewElement(
                args.cosplayId,
                navController
            )
        }
        composable<NewTask> { backStackEntry ->
            val args = backStackEntry.toRoute<NewTask>()
            NewTask(
                args.cosplayId,
                navController
            )
        }
        composable<EditElement> { backStackEntry ->
            val args = backStackEntry.toRoute<EditElement>()
            EditElement(
                elementId = args.elementId,
                navController = navController,
            )
        }
        composable<EditTask> { backStackEntry ->
            val args = backStackEntry.toRoute<EditTask>()
            EditTask(
                taskId = args.taskId,
                navController = navController,
            )
        }
        composable<EditPhoto> { backStackEntry ->
            val args = backStackEntry.toRoute<EditPhoto>()
            EditPhoto(
                photoId = args.photoId,
                navController = navController,
            )
        }
        composable<EditCosplay> { backStackEntry ->
            val args = backStackEntry.toRoute<EditCosplay>()
            EditCosplay(
                cosplayId = args.cosplayId,
                navController = navController,
            )
        }
    }
}
