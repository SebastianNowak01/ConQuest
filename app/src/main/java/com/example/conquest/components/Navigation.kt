import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.conquest.screens.MainScreen
import com.example.conquest.screens.SettingsScreen
import com.example.conquest.screens.NewCosplayScreen
import com.example.conquest.screens.SettingsScreenParams

@Composable
fun MainNavigation(navController: NavHostController) {
    val navController = navController
    NavHost(
        navController = navController,
        startDestination = MainScreen
    ) {
        composable<MainScreen> {
            MainScreen(navController)
        }
        composable<SettingsScreenParams> { it ->
            SettingsScreen(it)
        }
        composable<NewCosplayScreen> {
            NewCosplayScreen()
        }
    }
}
