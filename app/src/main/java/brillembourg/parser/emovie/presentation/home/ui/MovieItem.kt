package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MoviePresentationModel,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier
            .clickable { onClick?.invoke() },
        verticalArrangement = Arrangement.Center
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
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(10))
        )
        Text(
            modifier = Modifier.width(136.dp)
                .align(Alignment.CenterHorizontally),
            text = movie.name,
            textAlign = TextAlign.Center,
            color = Color.White
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