package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.data.network_imp.responses.MovieResponse
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.data.utils.extractDateFromString
import org.threeten.bp.LocalDate

data class MovieData(
    val id: Long,
    val name: String,
    val posterImageUrl: String? = null,
    val originalLanguage: String,
    val releaseDate: LocalDate,
    val backdropImageUrl: String?,
    val plot: String,
    val voteCount: Int,
    val voteAverage: Float
)

fun MovieData.toDomain(): Movie {
    return Movie(
        id = id,
        name = name,
        posterImageUrl = posterImageUrl,
        originalLanguage = originalLanguage,
        releaseYear = releaseDate,
        backdropImageUrl = backdropImageUrl,
        plot = plot,
        voteCount = voteCount,
        voteAverage = voteAverage
    )
}

fun Movie.toData(): MovieData {
    return MovieData(
        id = id,
        name = name,
        posterImageUrl = posterImageUrl,
        originalLanguage = originalLanguage,
        releaseDate = releaseYear,
        backdropImageUrl = backdropImageUrl,
        plot = plot,
        voteCount = voteCount,
        voteAverage = voteAverage
    )
}


