package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.getPosterCompleteUrl
import brillembourg.parser.emovie.presentation.theme.Shapes
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.threeten.bp.LocalDate

val movieFake = MoviePresentationModel(
    1L,
    "Name",
    "https://developer.android.com/static/images/home/billboard-compose-camp.png",
    "en",
    LocalDate.ofEpochDay(1000),
    "",
    "",
    4,
    6f
)

@Composable
fun MovieItem(movie: MoviePresentationModel, modifier: Modifier = Modifier) {
    Box(
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.getPosterCompleteUrl())
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.movie_placeholder),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .width(136.dp)
                .height(192.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(10))
        )
    }
}

@Preview(showBackground = false, widthDp = 400, heightDp = 400)
@Composable
fun MovieItemPreview() {
    eMovieTheme {
        MovieItem(movie = movieFake)
    }
}