package brillembourg.parser.emovie.domain.models

import org.threeten.bp.LocalDate

data class Movie(
    val id: Long,
    val name: String,
    val posterImageUrl: String? = null,
    val originalLanguage: String,
    val releaseYear: LocalDate,
    val backdropImageUrl: String?,
    val plot: String,
    val voteCount: Int,
    val voteAverage: Float
)

data class Trailer(
    val id: String,
    val key: String,
    val name: String,
    val site: String
)

data class MovieDetail(
    val movie: Movie,
    val trailers: List<Trailer>
)