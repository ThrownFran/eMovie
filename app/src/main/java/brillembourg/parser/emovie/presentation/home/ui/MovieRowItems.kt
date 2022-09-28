package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import org.threeten.bp.LocalDate

val movieListFake = (1..10).map {
    MoviePresentationModel(
        it.toLong(),
        "Name $it",
        "https://developer.android.com/static/images/home/billboard-compose-camp.png",
        "en",
        LocalDate.ofEpochDay(1000),
        "",
        "",
        4,
        6f
    )
}

@Composable
fun MovieRowItems(
    modifier: Modifier = Modifier,
    movies: List<MoviePresentationModel>,
    onMovieClick: ((MoviePresentationModel) -> Unit)? = null
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(movies, key = { item -> item.id }) { movie ->
            MovieItem(movie = movie, onClick = { onMovieClick?.invoke(movie) })
        }
    }
}

@Preview(showBackground = false, widthDp = 1000, heightDp = 400)
@Composable
fun MovieRowItemsPreview() {
    eMovieTheme {
        MovieRowItems(movies = movieListFake)
    }
}