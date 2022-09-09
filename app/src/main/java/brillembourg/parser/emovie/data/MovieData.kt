package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.data.network.MovieResponse
import brillembourg.parser.emovie.domain.Movie
import com.google.gson.annotations.SerializedName

data class MovieData(
    val id: Long,
    val name: String,
    val posterImageUrl: String
)

fun MovieData.toDomain(): Movie {
    return Movie(id, name, posterImageUrl)
}

fun Movie.toData(): MovieData {
    return MovieData(id, name, posterImageUrl)
}

fun MovieResponse.toData () : MovieData {
    return MovieData(id, name, posterImageUrl)
}

class MovieResponsee {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("vote_average")
    var rating = 0f

    @SerializedName("vote_count")
    var voteCount = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("poster_path")
    var posterPath: String? = null

    @SerializedName("backdrop_path")
    var backdropPath: String? = null

    @SerializedName("overview")
    var description: String? = null

    @SerializedName("release_date")
    var releaseDate: String? = null
}