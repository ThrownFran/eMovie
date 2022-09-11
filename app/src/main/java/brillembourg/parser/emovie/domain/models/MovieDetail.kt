package brillembourg.parser.emovie.domain.models

data class MovieDetail(
    val movie: Movie,
    val trailers: List<Trailer>
)