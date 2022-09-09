package brillembourg.parser.emovie.utils

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Movie
import org.threeten.bp.LocalDate
import java.util.*

val date = LocalDate.now()

val movieDataFakes = listOf(
    MovieData(1L, "Movie 1", "","en", date,),
    MovieData(2L, "Movie 1", "","en", date),
    MovieData(3L, "Movie 1", "","en", date),
    MovieData(4L, "Movie 1", "","", date)
)

val movieDomainFakes = listOf(
    Movie(1L, "Movie 1", "","en", date),
    Movie(2L, "Movie 1", "","en", date),
    Movie(3L, "Movie 1", "","en", date),
    Movie(4L, "Movie 1", "","en", date)
)