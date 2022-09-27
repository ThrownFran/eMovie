package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import org.threeten.bp.LocalDate


@Composable
fun MovieVerticalGridItems(movies: List<MoviePresentationModel>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Adaptive(150.dp)
    )
     {
        items(movies, key = { item -> item.id }) { movie ->
            MovieItem(movie = movie)
        }
    }
}

@Preview(showBackground = false, widthDp = 500, heightDp = 600)
@Composable
fun MovieVerticalGridItemsPreview() {
    eMovieTheme {
        MovieVerticalGridItems(movies = movieListFake)
    }
}