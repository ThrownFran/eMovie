package brillembourg.parser.emovie.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brillembourg.parser.emovie.domain.use_cases.GetMovieDetailUseCase
import brillembourg.parser.emovie.domain.use_cases.RefreshMovieDetailUseCase
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.toPresentation
import brillembourg.parser.emovie.presentation.utils.Logger
import brillembourg.parser.emovie.presentation.utils.getMessageFromException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val refreshMovieDetailUseCase: RefreshMovieDetailUseCase
) : ViewModel() {

    private val _detailUiState = MutableStateFlow(
        value = DetailUiState(
            movie = savedStateHandle.get<MoviePresentationModel>("movie")
                ?: throw IllegalArgumentException("Movie not provided to detail")
        ).also { state ->
            observeMovie(state.movie.id)
        })

    val detailUiState = _detailUiState.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        onError(throwable)
    }

    private fun onError(throwable: Throwable) {
        Logger.error(throwable)
        _detailUiState.update {
            it.copy(
                messageToShow = getMessageFromException(throwable),
                isLoading = false
            )
        }
    }

    init {
        refreshMovieDetail()
    }

    private fun refreshMovieDetail() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            refreshMovieDetailUseCase.invoke(detailUiState.value.movie.id)
            hideLoading()
        }
    }

    private fun showLoading() {
        _detailUiState.update { it.copy(isLoading = true) }
    }

    private fun hideLoading() {
        _detailUiState.update { it.copy(isLoading = false) }
    }

    private fun observeMovie(id: Long) {
        getMovieDetailUseCase.invoke(id)
            .onEach { movieDetail ->
                _detailUiState.update {
                    it.copy(
                        movie = movieDetail.movie.toPresentation(),
                        trailers = movieDetail.trailers
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onMessageShown() {
        _detailUiState.update { it.copy(messageToShow = null) }
    }

    fun onRefresh() {
        refreshMovieDetail()
    }


}