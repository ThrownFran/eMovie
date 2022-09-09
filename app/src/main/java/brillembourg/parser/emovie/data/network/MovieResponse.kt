package brillembourg.parser.emovie.data.network

import com.google.gson.annotations.SerializedName

class MovieListResponse {
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
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("release_date")
    val releaseDate: String
)