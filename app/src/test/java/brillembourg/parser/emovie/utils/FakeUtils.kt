package brillembourg.parser.emovie.utils

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.models.MovieDetail
import brillembourg.parser.emovie.domain.models.Trailer
import org.threeten.bp.LocalDate

data class MovieFake(
    val id: Long,
    val date: LocalDate = LocalDate.ofYearDay(1993, 1),
    val language: String = "en"
)

fun MovieFake.toDomain(): Movie {
    return Movie(id, "", "", language, date, "", "plot", 200, 3.4f)
}

data class TrailerFake(val id: String, val movieId: Long = 3L)

fun TrailerFake.toDomain(): Trailer {
    return Trailer(id, "key", "name", "youtube", movieId)
}

val date: LocalDate = LocalDate.ofYearDay(1993, 1)

val movieDataFakes = listOf(
    MovieData(1L, "Movie 1", "", "en", date, null, "plot", 200, 9.5f),
    MovieData(2L, "Movie 1", "", "en", date, null, "plot", 200, 3.2f),
    MovieData(3L, "Movie 1", "", "en", date, null, "plot", 200, 5.3f),
    MovieData(4L, "Movie 1", "", "", date, null, "plot", 200, 6.6f)
)

val movieDomainFakes = listOf(
    Movie(1L, "Movie 1", "", "en", date, null, "plot", 200, 9.5f),
    Movie(2L, "Movie 1", "", "en", date, null, "plot", 200, 9.5f),
    Movie(3L, "Movie 1", "", "en", date, null, "plot", 200, 9.5f),
    Movie(4L, "Movie 1", "", "en", date, null, "plot", 200, 9.5f)
)

val movieDetailFake = MovieDetail(
    movie = Movie(
        id = 1L,
        name = "Movie 1",
        posterImageUrl = "",
        originalLanguage = "en",
        releaseYear = date,
        backdropImageUrl = "",
        plot = "Plot",
        voteCount = 143,
        voteAverage = 9.2f
    ),
    trailers = listOf(
        Trailer("1L", "kdfseref", "Trailer 1", "Youtube",1L),
        Trailer("2L", "kdfseref", "Trailer 2", "Youtube",2L),
    )
)

val trailerFakes = listOf(
    Trailer("1", "Key1", "Trailer1", "Youtube", 1L),
    Trailer("2", "Key2", "Trailer2", "Youtube", 2L),
    Trailer("3", "Key3", "Trailer3", "Youtube", 3L),
    Trailer("4", "Key4", "Trailer4", "Youtube", 4L),
)