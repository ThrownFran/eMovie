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
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ context, throwable -> }

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
            }
            .launchIn(viewModelScope)
    }


}