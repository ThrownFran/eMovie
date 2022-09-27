package brillembourg.parser.emovie.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = Yellow,
    secondary = Teal200,
    surface = Black,
    background = Black,
    onPrimary = White,
    onSecondary = White,
    onSurface = White,
    onBackground = White
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    secondary = Teal200,
    surface = White,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun eMovieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val colors = if (darkTheme) {
//        DarkColorPalette
//    } else {
//        LightColorPalette
//    }

    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}