package brillembourg.parser.emovie.presentation.home

import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.presentation.MoviePresentationModel

data class HomeUiState(
    val topRatedMovies: List<MoviePresentationModel> = emptyList(),
    val upcomingMovies: List<MoviePresentationModel> = emptyList(),
    val recommendedMovies: RecommendedMovies = RecommendedMovies(),
)

data class RecommendedMovies(
    val currentYear: Int? = null,
    val currentLanguage: String? = null,
    val selectableYears : List<Int> = emptyList(),
    val selectableLanguages: List<String> = emptyList(),
    val movies: List<MoviePresentationModel> = emptyList()
)

