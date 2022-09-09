package brillembourg.parser.emovie.presentation.home

import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.presentation.MoviePresentationModel

data class HomeUiState(
    val topRatedMovies: List<MoviePresentationModel> = emptyList(),
    val upcomingMovies: List<MoviePresentationModel> = emptyList(),
    val recommendedMovies: RecommendedMovies = RecommendedMovies()
)

data class RecommendedMovies(
    val currentYear: String = "2022",
    val currentLanguage: String = "en",
    val movies: List<MoviePresentationModel> = emptyList()
)

