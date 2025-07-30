import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.conquest.screens.MainCosplayScreen
import com.example.conquest.screens.MainScreen
import com.example.conquest.screens.NewCosplayElementScreen
import com.example.conquest.screens.SettingsScreen
import com.example.conquest.screens.NewCosplayScreen
import com.example.conquest.screens.NewCosplayTaskScreen
import com.example.conquest.screens.SettingsScreenParams

@Composable
fun MainNavigation(navController: NavHostController, searchQuery: String) {
    val navController = navController
    NavHost(
        navController = navController, startDestination = MainScreen
    ) {
        composable<MainScreen> {
            MainScreen(navController, searchQuery)
        }
        composable<SettingsScreenParams> {
            SettingsScreen()
        }
        composable<NewCosplayScreen> {
            NewCosplayScreen(navController)
        }
        composable<MainCosplayScreen> { backStackEntry ->
            MainCosplayScreen(backStackEntry, navController)
        }
        composable<NewCosplayElementScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NewCosplayElementScreen>()
            NewCosplayElementScreen(args.cosplayId, navController)
        }
        composable<NewCosplayTaskScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NewCosplayTaskScreen>()
            NewCosplayTaskScreen(args.cosplayId, navController)
        }
    }
}
