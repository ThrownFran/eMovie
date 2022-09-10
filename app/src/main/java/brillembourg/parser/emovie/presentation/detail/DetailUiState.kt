package brillembourg.parser.emovie.presentation.detail

import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel

data class DetailUiState(
    val movie: MoviePresentationModel,
    val trailers: List<Trailer> = emptyList()
)




