package brillembourg.parser.emovie.presentation.home

import brillembourg.parser.emovie.presentation.models.MoviePresentationModel

data class HomeUiState(
    val topRatedMovies: List<MoviePresentationModel> = emptyList(),
    val upcomingMovies: List<MoviePresentationModel> = emptyList(),
    val recommendedMovies: RecommendedMovies = RecommendedMovies(),
    val navigateToThisMovie: MoviePresentationModel? = null
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

//sealed class YearSelection () {
//    abstract fun toDisplayString(): String
//
//    class AllYears: YearSelection() {
//        override fun toDisplayString(): String {
//            return "All years"
//        }
//    }
//    class Year(val int: Int): YearSelection() {
//        override fun toDisplayString(): String {
//            return int.toString()
//        }
//    }
//}

