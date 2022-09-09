package brillembourg.parser.emovie.presentation

import brillembourg.parser.emovie.domain.Movie

data class MoviePresentationModel (val id: Long, val name: String, val posterImageUrl: String)

fun MoviePresentationModel.toDomain () : Movie {
    return Movie(id,name,posterImageUrl)
}

fun Movie.toPresentation (): MoviePresentationModel {
    return MoviePresentationModel(id,name,posterImageUrl)
}