@file:OptIn(ExperimentalMaterial3Api::class)

package brillembourg.parser.emovie.presentation.detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.presentation.detail.DetailFragment
import brillembourg.parser.emovie.presentation.detail.DetailUiState
import brillembourg.parser.emovie.presentation.detail.DetailViewModel
import brillembourg.parser.emovie.presentation.home.ui.MainSnackBar
import brillembourg.parser.emovie.presentation.home.ui.movieListFake
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.asString
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

val movie = movieListFake[0]
val detailUiState = DetailUiState(movie = movieWithLargePlot, trailers = (1..10).map {
    Trailer(
        id = it.toString(), key = "$it", name = "Trailer $it", site = "", movieId = it.toLong()
    )
})

@Composable
fun DetailScreen(viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    DetailContent(lifecycleOwner = LocalLifecycleOwner.current,
        uiState = viewModel.detailUiState.collectAsState().value,
        onClickBack = { },
        onRefresh = { viewModel.onRefresh() },
        onMessageShown = { viewModel.onMessageShown() })
}

@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner,
    uiState: DetailUiState,
    onClickBack: () -> Unit,
    onRefresh: () -> Unit,
    onMessageShown: () -> Unit,
) {

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = uiState.isLoading),
        onRefresh = onRefresh
    ) {
        Scaffold(
            snackbarHost = {
                MainSnackBar(
                    messageToShow = uiState.messageToShow?.asString(LocalContext.current),
                    onMessageShown = onMessageShown
                )
            }
        ) {
            it.toString()
            MovieCollapsableScaffold(
                modifier = modifier,
                movie = uiState.movie,
                onClickBack = onClickBack
            ) {
                Surface {
                    LazyColumn {
                        item {
                            MovieChipRow(
                                modifier = Modifier.padding(vertical = 4.dp), movie = uiState.movie
                            )
                        }

                        item {
                            MoviePlot(
                                modifier = Modifier.padding(16.dp), movie = uiState.movie
                            )
                        }

                        item {
                            MovieTrailer(
                                lifecycleOwner = lifecycleOwner, trailers = uiState.trailers
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun MovieChipRow(modifier: Modifier, movie: MoviePresentationModel) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MovieChip(text = movie.getReleaseYear().toString())

        Spacer(modifier = Modifier.width(12.dp))

        MovieChip(text = movie.originalLanguage)

        Spacer(modifier = Modifier.width(12.dp))

        MovieChip(
            text = movie.voteAverage.toString(), imageVector = Icons.Outlined.Star
        )
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 1000)
@Composable
fun DetailScreenPreview() {
    eMovieTheme {
        DetailContent(
            lifecycleOwner = DetailFragment(),
            uiState = detailUiState,
            onClickBack = {},
            onRefresh = {},
            onMessageShown = {}
        )
    }
}
