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

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            eMovieTheme {
                EMovieApp()
            }
        }
    }

}