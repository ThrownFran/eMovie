package brillembourg.parser.emovie.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import brillembourg.parser.emovie.presentation.detail.ui.DetailScreen
import brillembourg.parser.emovie.presentation.home.ui.HomeScreen
import brillembourg.parser.emovie.presentation.utils.Feature
import brillembourg.parser.emovie.presentation.utils.NavCommand
import brillembourg.parser.emovie.presentation.utils.composable

@Composable
fun EMovieApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavCommand.ContentType(Feature.Home).route) {

        composable(NavCommand.ContentType(Feature.Home)) {
            HomeScreen {
                navController.navigate(NavCommand.ContentTypeDetail(Feature.Detail).createRoute(it.id)) {
                    popUpTo(NavCommand.ContentType(Feature.Home).route)
                }
            }
        }

        composable(
            NavCommand.ContentTypeDetail(Feature.Detail)
        ) {
            DetailScreen {
                navController.navigateUp()
            }
        }

    }
}