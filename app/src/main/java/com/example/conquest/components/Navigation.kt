import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.conquest.screens.MainCosplayScreen
import com.example.conquest.screens.MainScreen
import com.example.conquest.screens.SettingsScreen
import com.example.conquest.screens.NewCosplayScreen
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
            MainCosplayScreen(backStackEntry)
        }
    }
}
