package brillembourg.parser.emovie.data.network_imp.responses

import com.google.gson.annotations.SerializedName

class GetMoviesResponse {
    @SerializedName("results")
    var results : List<MovieResponse>? = null
}

class MovieResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val name: String,
    @SerializedName("poster_path")
    val posterImageUrl: String,
    @SerializedName("backdrop_path")
    val backDropImageUrl: String,
    @SerializedName("overview")
    val plot: String,
    @SerializedName("vote_average")
    val voteAverage: Float,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("release_date")
    val releaseDate: String
)