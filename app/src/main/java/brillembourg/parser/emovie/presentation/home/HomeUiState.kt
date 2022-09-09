package brillembourg.parser.emovie.presentation.home

import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.presentation.MoviePresentationModel

data class HomeUiState(
    val topRatedMovies: List<MoviePresentationModel> = emptyList(),
    val upcomingMovies: List<MoviePresentationModel> = emptyList()
)