@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.home.LanguageFilter
import brillembourg.parser.emovie.presentation.home.RecommendedMovies
import brillembourg.parser.emovie.presentation.home.YearFilter
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

val upcomingMovies = movieListFake
val topRatedMovies = movieListFake.shuffled()
val recommendedMovies = RecommendedMovies(
    movies = topRatedMovies.take(6), yearFilter = YearFilter(
        currentYear = null, selectableYears = listOf(1940, 1888, 2000, 2020, 2022)
    ), languageFilter = LanguageFilter(
        currentLanguage = null, selectableLanguages = listOf("es", "en", "ja", "it")
    )
)

@Composable
fun HomeScreen(
    upcomingMovies: List<MoviePresentationModel>,
    isLoadingMoreUpcomingMovies: Boolean = false,
    onEndReachedForUpcomingMovies: (position: Int) -> Unit,
    topRatedMovies: List<MoviePresentationModel>,
    isLoadingMoreTopRatedMovies: Boolean = false,
    onEndReachedForTopRatedMovies: (position: Int) -> Unit,
    recommendedMovies: RecommendedMovies,
    onMovieClick: ((MoviePresentationModel) -> Unit)? = null,
    onFilterYearChanged: (Int?) -> Unit,
    onFilterLanguageChanged: (String?) -> Unit,
    messageToShow: String?,
    onMessageShown: () -> Unit,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = { onRefresh() },
    ) {
        Scaffold(
            topBar = { MainAppBar() },
            snackbarHost = { MainSnackBar(messageToShow, onMessageShown) }) { paddingValues ->

            LazyVerticalGrid(
                contentPadding = paddingValues,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Adaptive(150.dp),
            ) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        HomeSection(title = stringResource(id = R.string.upcoming)) {
                            MovieRowItems(
                                movies = upcomingMovies,
                                onMovieClick = onMovieClick,
                                onEndReached = onEndReachedForUpcomingMovies,
                                isLoadingMore = isLoadingMoreUpcomingMovies
                            )
                            EmptyMoviesText(upcomingMovies)
                        }

                        HomeSection(title = stringResource(id = R.string.toprated)) {
                            MovieRowItems(
                                movies = topRatedMovies,
                                onMovieClick = onMovieClick,
                                onEndReached = onEndReachedForTopRatedMovies,
                                isLoadingMore = isLoadingMoreTopRatedMovies
                            )
                            EmptyMoviesText(upcomingMovies)
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    HomeSection(title = stringResource(id = R.string.recommended_movies)) {
                        RecommendedMovieFilters(
                            recommendedMovies = recommendedMovies,
                            onYearValueChanged = onFilterYearChanged,
                            onLanguageValueChanged = onFilterLanguageChanged
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        EmptyMoviesText(recommendedMovies.movies)
                    }
                }

                items(recommendedMovies.movies, key = { item -> item.id }) { movie ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxSize()
                            .wrapContentWidth()
                            .animateItemPlacement(tween(300))
                    ) {
                        MovieItem(movie = movie, onClick = { onMovieClick?.invoke(movie) })
                    }
                }
            }
        }
    }

}

@Composable
private fun EmptyMoviesText(movies: List<MoviePresentationModel>) {
    AnimatedVisibility(visible = movies.isEmpty()) {
        Text(
            text = stringResource(id = R.string.no_movies_found),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun RecommendedMovieFilters(
    recommendedMovies: RecommendedMovies,
    onYearValueChanged: (Int?) -> Unit,
    onLanguageValueChanged: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {

        val allYears = stringResource(id = R.string.all_years)
        val years = recommendedMovies.yearFilter.selectableYears.map { it.toString() }

        val allLanguages = stringResource(id = R.string.all_languages)
        val languages = recommendedMovies.languageFilter.selectableLanguages

        FilterMenu(
            options = listOf(allYears) + years,
            label = stringResource(id = R.string.year),
            currentOption = recommendedMovies.yearFilter.currentYear?.toString() ?: allYears,
            onOptionSelected = { optionSelected ->
                onYearValueChanged.invoke(
                    if (optionSelected == allYears) null
                    else optionSelected.toInt()
                )
            },
            modifier = Modifier.wrapContentWidth(Alignment.Start)
        )

        Spacer(modifier = Modifier.width(16.dp))

        FilterMenu(
            options = listOf(allLanguages) + languages,
            label = stringResource(id = R.string.language),
            currentOption = recommendedMovies.languageFilter.currentLanguage ?: allLanguages,
            onOptionSelected = { optionSelected ->
                onLanguageValueChanged.invoke(
                    if (optionSelected == allLanguages) null
                    else optionSelected
                )
            },
            modifier = Modifier
                .wrapContentWidth(Alignment.Start)
                .width(IntrinsicSize.Min),
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val testEmptyMovies = false
    eMovieTheme {
        HomeScreen(
            upcomingMovies = if (testEmptyMovies) emptyList() else upcomingMovies,
            isLoadingMoreUpcomingMovies = true,
            topRatedMovies = if (testEmptyMovies) emptyList() else topRatedMovies,
            recommendedMovies = recommendedMovies.run {
                this.copy(movies = emptyList())
            },
            onFilterLanguageChanged = {},
            onFilterYearChanged = {},
            messageToShow = "Error",
            isLoading = true,
            onRefresh = {},
            onMessageShown = {},
            onEndReachedForTopRatedMovies = {},
            onEndReachedForUpcomingMovies = {})
    }
}