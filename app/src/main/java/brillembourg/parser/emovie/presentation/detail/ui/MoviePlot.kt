package brillembourg.parser.emovie.presentation.detail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.home.ui.MovieItem
import brillembourg.parser.emovie.presentation.home.ui.movieFake
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.theme.eMovieTheme

val movieWithLargePlot =
    movieFake.copy(plot = "Suuuuuupeer long plort,Suuuuuupeer long plortSuuuuuupeer long plortSuuuuuupeer long plortSuuuuuupeer long plortSuuuuuupeer long plortSuuuuuupeer long plortSuuuuuupeer long plortSuuuuuupeer long plortSuuuuuupeer long plort")

@Composable
fun MoviePlot(modifier: Modifier = Modifier, movie: MoviePresentationModel) {
    Column(modifier = modifier.fillMaxSize()) {

//        val annotatedString = buildAnnotatedString {
//            appendInlineContent(id = "imageId")
//            append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAbbbbbb")
//            append(" with a call iconwith a call iconwith a call iconwith a call iconwith a call iconwith a call iconwith a call iconwith a call iconwith a call iconwith a call icon")
//        }
//        val inlineContentMap = mapOf(
//            "imageId" to InlineTextContent(
//                Placeholder(80.sp, 80.sp, PlaceholderVerticalAlign.TextTop)
//            ) {
////                Image(
////                    painter = painterResource(id = R.drawable.movie_placeholder),
////                    contentDescription = null
////                )
//                MovieItem(
////                    modifier = Modifier.padding(8.dp),
//                    movie = movie
//                )
//            }
//        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.movie_plot),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))


        Row {
            MovieItem(movie = movie)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = movie.plot)
        }

//        Text(
//            color = Color.White,
//            style = MaterialTheme.typography.bodyLarge,
//            inlineContent = inlineContentMap,
//            text = annotatedString
//        )


    }
}

@Preview(showBackground = true)
@Composable
fun MoviePlotPreview() {
    eMovieTheme {
        Surface {
            MoviePlot(movie = movieWithLargePlot)
        }
    }
}