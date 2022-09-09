package brillembourg.parser.emovie.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.GetMoviesUseCase
import brillembourg.parser.emovie.domain.RefreshMoviesUseCase
import brillembourg.parser.emovie.domain.Schedulers
import brillembourg.parser.emovie.presentation.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val schedulers: Schedulers,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val refreshMoviesUseCase: RefreshMoviesUseCase
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeUiState()).also {
        observeTopRatedMovies()
        observeUpcomingMovies()
    }
    val homeState = _homeState.asStateFlow()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable -> }

    init {
        refreshUpcomingMovies()
        refreshTopRatedMovies()
    }

    private fun refreshUpcomingMovies() {
        viewModelScope.launch(coroutineExceptionHandler) {
            refreshMoviesUseCase.invoke(Category.Upcoming())
        }
    }

    private fun refreshTopRatedMovies() {
        viewModelScope.launch(coroutineExceptionHandler) {
            refreshMoviesUseCase.invoke(Category.TopRated())
        }
    }

    private fun observeUpcomingMovies() {
        getMoviesUseCase.invoke(Category.Upcoming())
            .map { list -> list.map { movie -> movie.toPresentation() } }
            .onEach { movies ->
                _homeState.update { it.copy(upcomingMovies = movies) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeTopRatedMovies() {
        getMoviesUseCase.invoke(Category.TopRated())
            .map { list -> list.map { movie -> movie.toPresentation() } }
            .onEach { movies ->
                _homeState.update { it.copy(topRatedMovies = movies) }
                filterRecommendedMovies()
            }
            .launchIn(viewModelScope)
    }

    private fun filterRecommendedMovies() {
        val movies = homeState.value.topRatedMovies
        val languages: List<String> =
            homeState.value.topRatedMovies.map { it.originalLanguage }
        val yearList: List<Int> = homeState.value.topRatedMovies.map { it.getReleaseYear() }


        val currentYear = _homeState.value.recommendedMovies.yearFilter.currentYear
        val currentLanguage = homeState.value.recommendedMovies.languageFilter.currentLanguage

        val filteredList = movies
            .filter {

                if(currentLanguage == null && currentYear == null) return@filter true

                if (currentYear == null) {
                    return@filter it.originalLanguage == currentLanguage
                }
                if(currentLanguage == null) {
                    return@filter it.getReleaseYear() == currentYear
                }

                (it.getReleaseYear() == currentYear) && it.originalLanguage == currentLanguage
        }.take(6)

        val selectableYears: List<Int> = yearList.distinct().sortedByDescending { it }

        _homeState.update {
            it.copy(
                recommendedMovies = it.recommendedMovies.copy(
                    yearFilter = it.recommendedMovies.yearFilter.copy(selectableYears = selectableYears),
                    languageFilter = it.recommendedMovies.languageFilter.copy(
                        selectableLanguages = languages.distinct().sortedDescending()
                    ),
                    movies = filteredList
                )
            )
        }
    }

    fun onLanguageFilterSelected(language: String?) {
        _homeState.update {
            it.copy(
                recommendedMovies = it.recommendedMovies.copy(
                        languageFilter = it.recommendedMovies.languageFilter.copy(
                                currentLanguage = language
                            )
                    )
            )
        }
        filterRecommendedMovies()
    }

    fun onSetNoLanguageFilter() {
        onLanguageFilterSelected(null)
    }

    fun onYearFilterSelected(year: Int?) {
        _homeState.update {
            it.copy(
                recommendedMovies = it.recommendedMovies.copy(
                    yearFilter = it.recommendedMovies.yearFilter.copy(currentYear = year)
                )
            )
        }

        filterRecommendedMovies()
    }

    fun onSetNoYearFilter() {
        onYearFilterSelected(null)
    }




}