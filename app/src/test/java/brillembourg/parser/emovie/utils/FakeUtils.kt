package brillembourg.parser.emovie.utils

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Movie
import org.threeten.bp.LocalDate

val date = LocalDate.ofYearDay(1993,1)

val movieDataFakes = listOf(
    MovieData(1L, "Movie 1", "", "en", date, null,,,,),
    MovieData(2L, "Movie 1", "", "en", date, null,,,),
    MovieData(3L, "Movie 1", "", "en", date, null,,,),
    MovieData(4L, "Movie 1", "", "", date, null,,,)
)

val movieDomainFakes = listOf(
    Movie(1L, "Movie 1", "","en", date),
    Movie(2L, "Movie 1", "","en", date),
    Movie(3L, "Movie 1", "","en", date),
    Movie(4L, "Movie 1", "","en", date)
)