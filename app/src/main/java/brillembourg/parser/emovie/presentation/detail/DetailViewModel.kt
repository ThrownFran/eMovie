package brillembourg.parser.emovie.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brillembourg.parser.emovie.domain.Schedulers
import brillembourg.parser.emovie.domain.use_cases.GetMovieDetailUseCase
import brillembourg.parser.emovie.domain.use_cases.RefreshMovieDetailUseCase
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val schedulers: Schedulers,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val refreshMovieDetailUseCase: RefreshMovieDetailUseCase
) : ViewModel() {

    private val _detailUiState = MutableStateFlow(
        value = DetailUiState(
            movie = savedStateHandle.get<MoviePresentationModel>("movie")
                ?: throw IllegalArgumentException("Movie not provided to detail")
        ).also { state ->
            observeMovie(state.movie.id)
            refreshMovieDetail(state.movie.id)
        })

    val detailUiState = _detailUiState.asStateFlow()

    private fun refreshMovieDetail(id: Long) {
        viewModelScope.launch {
            refreshMovieDetailUseCase.invoke(id)
        }
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


}