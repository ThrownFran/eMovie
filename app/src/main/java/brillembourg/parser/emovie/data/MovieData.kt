package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.domain.Movie

data class MovieData(val id: Long, val name: String, val backdropImageUrl: String)

fun MovieData.toDomain (): Movie {
    return Movie(id,name,backdropImageUrl)
}

fun Movie.toData (): MovieData {
    return MovieData(id,name,backdropImageUrl)
}