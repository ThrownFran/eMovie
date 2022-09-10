package brillembourg.parser.emovie.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brillembourg.parser.emovie.domain.Schedulers
import brillembourg.parser.emovie.domain.use_cases.GetMovieDetailUseCase
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val schedulers: Schedulers,
    val getMovieDetailUseCase: GetMovieDetailUseCase
) : ViewModel() {

    private val _detailUiState = MutableStateFlow(
        value = DetailUiState(
            movie = savedStateHandle.get<MoviePresentationModel>("movie")
                ?: throw IllegalArgumentException("Movie not provided to detail")
        ).also {
            getMovieDetailUseCase.invoke(it.movie.id)
                .launchIn(viewModelScope)
        })

    val detailUiState = _detailUiState.asStateFlow()




}