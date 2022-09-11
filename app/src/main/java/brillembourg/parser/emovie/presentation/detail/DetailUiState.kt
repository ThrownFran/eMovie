package brillembourg.parser.emovie.presentation.detail

import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.UiText


data class DetailUiState(
    val movie: MoviePresentationModel,
    val trailers: List<Trailer> = emptyList(),
    val messageToShow: UiText? = null,
    val isLoading: Boolean = false
)
