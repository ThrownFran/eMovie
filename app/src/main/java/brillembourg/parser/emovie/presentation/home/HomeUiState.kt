package brillembourg.parser.emovie.presentation.home

import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.UiText

data class HomeUiState(
    val topRatedMovies: List<MoviePresentationModel> = emptyList(),
    val isLoadingMoreTopRatedMovies: Boolean = false,
    val isLastTopRatedPageReached: Boolean = false,

    val upcomingMovies: List<MoviePresentationModel> = emptyList(),
    val isLoadingMoreUpcomingMovies: Boolean = false,
    val isLastUpcomingPageReached: Boolean = false,

    val recommendedMovies: RecommendedMovies = RecommendedMovies(),
    val navigateToThisMovie: MoviePresentationModel? = null,
    val isLoading : Boolean = false,
    val messageToShow : UiText? = null
)

data class RecommendedMovies(
    val yearFilter: YearFilter = YearFilter(),
    val languageFilter: LanguageFilter = LanguageFilter(),
    val movies: List<MoviePresentationModel> = emptyList(),
)

data class YearFilter(
    val currentYear: Int? = null,
    val selectableYears: List<Int> = emptyList(),
)

data class LanguageFilter(
    val currentLanguage: String? = null,
    val selectableLanguages: List<String> = emptyList()
)


