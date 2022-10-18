package brillembourg.parser.emovie.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.use_cases.GetMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RefreshMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RequestNextMoviePageUseCase
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.toPresentation
import brillembourg.parser.emovie.presentation.utils.getMessageFromException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val refreshMoviesUseCase: RefreshMoviesUseCase,
    private val requestNextMoviePageUseCase: RequestNextMoviePageUseCase,
) : ViewModel() {

    sealed interface UiAction {
        data class SelectYear(val year: Int? = null) : UiAction
        data class SelectLanguage(val language: String? = null) : UiAction
        data class EndOfPageReached(val category: Category, val lastVisibleItem: Int) : UiAction
        data class MovieClicked(val movie: MoviePresentationModel) : UiAction
        object Refresh: UiAction
    }

    private val recommendedMovieCount = 6

    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
        .also { observeMoviesStream() }
    val homeUiState = _homeUiState.asStateFlow()

    private val actionStateFlow = MutableSharedFlow<UiAction>()
    val onAction: (UiAction) -> Unit = { uiAction ->
        viewModelScope.launch { actionStateFlow.emit(uiAction) }
    }

    val recommendedMovies: StateFlow<RecommendedMovies> = recommendedMoviesStream()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    init {
        refreshMovieData()
        setupActions()
    }

    private fun setupActions() {
        actionEndOfPage()
        actionMovieClicked()
        actionRefresh()
    }

    private fun actionRefresh() {
        viewModelScope.launch {
            actionStateFlow
                .filterIsInstance<UiAction.Refresh>()
                .collectLatest {
                    refreshMovieData()
                }
        }
    }

    private fun actionMovieClicked() {
        viewModelScope.launch {
            actionStateFlow
                .filterIsInstance<UiAction.MovieClicked>()
                .distinctUntilChanged()
                .collectLatest { movieClickedAction ->
                    _homeUiState.update { it.copy(navigateToThisMovie = movieClickedAction.movie) }
                }
        }
    }

    private fun observeMoviesStream() {

        getMoviesUseCase.invoke(Category.TopRated)
            .map { list -> list.map { movie -> movie.toPresentation() } }.onEach { movies ->
                _homeUiState.update { it.copy(topRatedMovies = movies) }
            }
            .catch { t -> onError(t) }
            .launchIn(viewModelScope)

        getMoviesUseCase.invoke(Category.Upcoming)
            .map { list -> list.map { movie -> movie.toPresentation() } }.onEach { movies ->
                _homeUiState.update { it.copy(upcomingMovies = movies) }
            }
            .catch { t -> onError(t) }.launchIn(viewModelScope)
    }

    private fun actionEndOfPage() {
        viewModelScope.launch {
            actionStateFlow
                .filterIsInstance<UiAction.EndOfPageReached>()
                .distinctUntilChanged()
                .collectLatest { endOfPageReached ->
                    requestNextPage(
                        category = endOfPageReached.category,
                        lastVisibleItem = endOfPageReached.lastVisibleItem
                    )
                }
        }
    }

    private fun recommendedMoviesStream(): StateFlow<RecommendedMovies> {

        val selectYearFlow =
            actionStateFlow
                .filterIsInstance<UiAction.SelectYear>()
                .distinctUntilChanged()
                .onStart { emit(UiAction.SelectYear()) }

        val selectLanguageFlow =
            actionStateFlow
                .filterIsInstance<UiAction.SelectLanguage>()
                .distinctUntilChanged()
                .onStart { emit(UiAction.SelectLanguage()) }

        return combine(selectYearFlow,
            selectLanguageFlow,
            homeUiState) { yearFilter, languageFilter, uiState ->

            val movies = uiState.topRatedMovies
            val languages: List<String> = movies.map { it.originalLanguage }
            val yearList: List<Int> = movies.map { it.getReleaseYear() }

            val filteredList = movies.filter {

                if (languageFilter.language == null && yearFilter.year == null) return@filter true

                if (yearFilter.year == null) {
                    return@filter it.originalLanguage == languageFilter.language
                }
                if (languageFilter.language == null) {
                    return@filter it.getReleaseYear() == yearFilter.year
                }

                (it.getReleaseYear() == yearFilter.year) && it.originalLanguage == languageFilter.language
            }

            RecommendedMovies(
                yearFilter = YearFilter(
                    currentYear = yearFilter.year,
                    selectableYears = yearList.distinct().sortedByDescending { it }),
                languageFilter = LanguageFilter(
                    currentLanguage = languageFilter.language,
                    selectableLanguages = languages.distinct().sortedDescending()),
                movies = filteredList
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, RecommendedMovies())
    }

    private fun refreshMovieData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()

            val refreshTopRatedJob: Deferred<RefreshMoviesUseCase.Result> = async {
                refreshMoviesUseCase.invoke(Category.TopRated)
            }
            val refreshUpcomingJob = async {
                refreshMoviesUseCase.invoke(Category.Upcoming)
            }
            listOf(refreshUpcomingJob, refreshUpcomingJob).awaitAll()

            if (refreshTopRatedJob.await() == RefreshMoviesUseCase.Result.IsFirstAndLastPage) {
                _homeUiState.update { it.copy(isLastTopRatedPageReached = true) }
            }

            if (refreshUpcomingJob.await() == RefreshMoviesUseCase.Result.IsFirstAndLastPage) {
                _homeUiState.update { it.copy(isLastUpcomingPageReached = true) }
            }

            hideLoading()
        }
    }

    private fun onError(throwable: Throwable) {
        _homeUiState.update {
            it.copy(messageToShow = getMessageFromException(throwable), isLoading = false)
        }
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

    fun onNavigateToMovieCompleted() {
        _homeUiState.update { it.copy(navigateToThisMovie = null) }
    }

    private fun requestNextPage(category: Category, lastVisibleItem: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                updateIsLoadingMoreState(category, true)
                Timber.e("Request next page")
                val result = requestNextMoviePageUseCase.invoke(category, lastVisibleItem)
                updateIsLastPageState(category, result)
            } catch (e: Exception) {
                onError(e)
            } finally {
                updateIsLoadingMoreState(category, false)
            }
        }
    }

    private fun updateIsLastPageState(
        category: Category,
        result: RequestNextMoviePageUseCase.Result,
    ) {
        when (category) {
            Category.TopRated -> _homeUiState.update {
                it.copy(isLastTopRatedPageReached = result == RequestNextMoviePageUseCase.Result.LastPageAlreadyReached)
            }
            Category.Upcoming -> _homeUiState.update {
                it.copy(isLastUpcomingPageReached = result == RequestNextMoviePageUseCase.Result.LastPageAlreadyReached)
            }
        }
    }

    private fun updateIsLoadingMoreState(category: Category, isLoading: Boolean) {
        when (category) {
            Category.TopRated -> _homeUiState.update { it.copy(isLoadingMoreTopRatedMovies = isLoading) }
            Category.Upcoming -> _homeUiState.update { it.copy(isLoadingMoreUpcomingMovies = isLoading) }
        }
    }


}