@file:OptIn(ExperimentalMaterial3Api::class)

package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.home.LanguageFilter
import brillembourg.parser.emovie.presentation.home.RecommendedMovies
import brillembourg.parser.emovie.presentation.home.YearFilter
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.theme.eMovieTheme

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
    topRatedMovies: List<MoviePresentationModel>,
    recommendedMovies: RecommendedMovies,
    onFilterYearChanged: (Int?) -> Unit,
    onFilterLanguageChanged: (String?) -> Unit
) {
    Scaffold(topBar = {
        MainAppBar()
    }) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {

            LazyVerticalGrid(
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Adaptive(150.dp)
            ) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        HomeSection(title = stringResource(id = R.string.upcoming)) {
                            MovieRowItems(movies = upcomingMovies)
                        }

                        HomeSection(title = stringResource(id = R.string.toprated)) {
                            MovieRowItems(movies = topRatedMovies)
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
                    }
                }

                items(recommendedMovies.movies, key = { item -> item.id }) { movie ->
                    MovieItem(movie = movie)
                }
            }


        }
    }
}

@Composable
private fun RecommendedMovieFilters(
    recommendedMovies: RecommendedMovies,
    onYearValueChanged: (Int?) -> Unit,
    onLanguageValueChanged: (String?) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        val years = recommendedMovies.yearFilter.selectableYears.map { it.toString() }
        val allYears = stringResource(id = R.string.all_years)
        FilterMenu(modifier = Modifier.weight(1f),
            options = listOf(allYears) + years,
            label = stringResource(id = R.string.year),
            currentOption = recommendedMovies.yearFilter.currentYear?.toString() ?: allYears,
            onOptionSelected = { optionSelected ->
                onYearValueChanged.invoke(
                    if(optionSelected == allYears) null
                    else optionSelected.toInt()
                )
            })

        Spacer(modifier = Modifier.width(16.dp))

        val languages = recommendedMovies.languageFilter.selectableLanguages
        val allLanguages = stringResource(id = R.string.all_languages)
        FilterMenu(modifier = Modifier.weight(1f),
            options = listOf(allLanguages) + languages,
            label = stringResource(id = R.string.language),
            currentOption = recommendedMovies.languageFilter.currentLanguage ?: allLanguages,
            onOptionSelected = { optionSelected ->
                onLanguageValueChanged.invoke(
                    if (optionSelected == allLanguages) null
                    else optionSelected
                )
            })
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    eMovieTheme {
        HomeScreen(
            upcomingMovies = upcomingMovies,
            topRatedMovies = topRatedMovies,
            recommendedMovies = recommendedMovies,
            onFilterLanguageChanged = {},
            onFilterYearChanged = {}
        )
    }
}