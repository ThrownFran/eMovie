package brillembourg.parser.emovie.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.use_cases.GetMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RefreshMoviesUseCase
import brillembourg.parser.emovie.core.Schedulers
import brillembourg.parser.emovie.domain.use_cases.RequestNextMoviePageUseCase
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.toPresentation
import brillembourg.parser.emovie.presentation.utils.getMessageFromException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val refreshMoviesUseCase: RefreshMoviesUseCase,
    private val requestNextMoviePageUseCase: RequestNextMoviePageUseCase,
) : ViewModel() {

    private val recommendedMovieCount = 6

    private val _homeUiState = MutableStateFlow(HomeUiState()).also {
        observeTopRatedMovies()
        observeUpcomingMovies()
    }
    val homeUiState = _homeUiState.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    init { refreshMovieData() }

    private fun refreshMovieData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()

            listOf(
                launch { refreshMoviesUseCase.invoke(Category.Upcoming()) },
                launch { refreshMoviesUseCase.invoke(Category.TopRated()) }
            ).joinAll()

            hideLoading()
        }
    }

    private fun onError(throwable: Throwable) {
        _homeUiState.update {
            it.copy(
                messageToShow = getMessageFromException(throwable),
                isLoading = false
            )
        }
    }

    private fun observeUpcomingMovies() {
        getMoviesUseCase.invoke(Category.Upcoming())
            .map { list -> list.map { movie -> movie.toPresentation() } }
            .onEach { movies ->
                _homeUiState.update { it.copy(upcomingMovies = movies) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeTopRatedMovies() {
        getMoviesUseCase.invoke(Category.TopRated())
            .map { list -> list.map { movie -> movie.toPresentation() } }
            .onEach { movies ->
                _homeUiState.update { it.copy(topRatedMovies = movies) }
                filterRecommendedMovies()
            }
            .launchIn(viewModelScope)
    }

    private fun hideLoading() {
        _homeUiState.update { it.copy(isLoading = false) }
    }

    private fun showLoading() {
        _homeUiState.update { it.copy(isLoading = true) }
    }

    fun onMessageShown() {
        _homeUiState.update { it.copy(messageToShow = null) }
    }

    private fun filterRecommendedMovies() {
        val movies = homeUiState.value.topRatedMovies

        val languages: List<String> = homeUiState.value.topRatedMovies.map { it.originalLanguage }
        val yearList: List<Int> = homeUiState.value.topRatedMovies.map { it.getReleaseYear() }

        val currentYear = _homeUiState.value.recommendedMovies.yearFilter.currentYear
        val currentLanguage = homeUiState.value.recommendedMovies.languageFilter.currentLanguage

        val filteredList = movies
            .filter {

                if (currentLanguage == null && currentYear == null) return@filter true

                if (currentYear == null) {
                    return@filter it.originalLanguage == currentLanguage
                }
                if (currentLanguage == null) {
                    return@filter it.getReleaseYear() == currentYear
                }

                (it.getReleaseYear() == currentYear) && it.originalLanguage == currentLanguage
            }.take(recommendedMovieCount)

        val selectableYears: List<Int> = yearList.distinct().sortedByDescending { it }

        _homeUiState.update {
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
        _homeUiState.update {
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
        _homeUiState.update {
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

    fun onMovieClick(movieClicked: MoviePresentationModel) {
        _homeUiState.update { it.copy(navigateToThisMovie = movieClicked) }
    }

    fun onNavigateToMovieCompleted() {
        _homeUiState.update { it.copy(navigateToThisMovie = null) }
    }

    fun onRefresh() {
        refreshMovieData()
    }

    fun onEndOfTopRatedMoviesReached(lastVisibleItem: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
//            showLoading()
            requestNextMoviePageUseCase.invoke(Category.TopRated(), lastVisibleItem)
//            hideLoading()
        }
    }


}