package com.example.conquest.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.conquest.screens.MainScreen
import com.example.conquest.screens.SettingsScreen
import com.example.conquest.screens.SettingsScreenParams
import com.example.conquest.screens.cosplay.EditCosplay
import com.example.conquest.screens.cosplay.EditElement
import com.example.conquest.screens.cosplay.EditEvent
import com.example.conquest.screens.cosplay.EditPhoto
import com.example.conquest.screens.cosplay.EditProgressPhoto
import com.example.conquest.screens.cosplay.EditTask
import com.example.conquest.screens.cosplay.Events
import com.example.conquest.screens.cosplay.EventsScreen
import com.example.conquest.screens.cosplay.MainCosplayScreen
import com.example.conquest.screens.cosplay.NewCosplay
import com.example.conquest.screens.cosplay.NewElement
import com.example.conquest.screens.cosplay.NewEvent
import com.example.conquest.screens.cosplay.NewTask
import com.example.conquest.screens.cosplay.Progress
import com.example.conquest.screens.cosplay.ProgressScreen

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
            EventsScreen(navController = navController)
        }
        composable<Progress> { backStackEntry ->
            val args = backStackEntry.toRoute<Progress>()
            ProgressScreen(
                navController = navController,
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
            MainCosplayScreen(backStackEntry, navController)
        }
        composable<NewElement> { backStackEntry ->
            val args = backStackEntry.toRoute<NewElement>()
            NewElement(args.cosplayId, navController)
        }
        composable<NewTask> { backStackEntry ->
            val args = backStackEntry.toRoute<NewTask>()
            NewTask(args.cosplayId, navController)
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
