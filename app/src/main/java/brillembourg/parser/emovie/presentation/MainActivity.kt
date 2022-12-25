package brillembourg.parser.emovie.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import brillembourg.parser.emovie.databinding.ActivityMainBinding
import brillembourg.parser.emovie.presentation.detail.ui.DetailContent
import brillembourg.parser.emovie.presentation.detail.ui.DetailScreen
import brillembourg.parser.emovie.presentation.home.ui.HomeScreen
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import brillembourg.parser.emovie.presentation.utils.Feature
import brillembourg.parser.emovie.presentation.utils.NavArg
import brillembourg.parser.emovie.presentation.utils.NavCommand
import brillembourg.parser.emovie.presentation.utils.composable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        setContent {
            eMovieTheme {
                EMovieApp()
            }
        }
    }

    @Composable
    fun EMovieApp() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = NavCommand.ContentType(Feature.Home).route) {

            composable(NavCommand.ContentType(Feature.Home)) {
                HomeScreen {
//                    navController.navigate(NavCommand.ContentTypeDetail(Feature.Detail).createRoute(it.id))
                }
            }

//            composable(
//               NavCommand.ContentTypeDetail(Feature.Detail)
//            ) {
//                val id = it.arguments?.getString(NavArg.ItemId.key)
//                DetailScreen()
//            }

        }
    }

}