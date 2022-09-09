package brillembourg.parser.emovie.domain

import org.threeten.bp.LocalDate

data class Movie(
    val id: Long,
    val name: String,
    val posterImageUrl: String? = null,
    val originalLanguage: String,
    val releaseYear: LocalDate
)