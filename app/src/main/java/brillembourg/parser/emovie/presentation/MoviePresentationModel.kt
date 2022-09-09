package brillembourg.parser.emovie.presentation

import brillembourg.parser.emovie.domain.Movie
import org.threeten.bp.LocalDate

data class MoviePresentationModel(
    val id: Long,
    val name: String,
    val posterImageUrl: String? = null,
    val originalLanguage: String,
    val releaseDate: LocalDate
) {

    fun getReleaseYear (): Int {
        return releaseDate.year
    }

}

fun MoviePresentationModel.toDomain(): Movie {
    return Movie(id, name, posterImageUrl, originalLanguage, releaseDate)
}

fun Movie.toPresentation(): MoviePresentationModel {
    return MoviePresentationModel(id, name, posterImageUrl, originalLanguage, releaseYear)
}