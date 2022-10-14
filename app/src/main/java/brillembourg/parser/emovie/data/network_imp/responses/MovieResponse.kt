package brillembourg.parser.emovie.data.network_imp.responses

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.utils.extractDateFromString
import com.google.gson.annotations.SerializedName

class GetMoviesResponse {
    @SerializedName("results")
    var results : List<MovieResponse>? = null
    @SerializedName("page")
    val currentPage = 0
    @SerializedName("total_pages")
    val lastPage = 0
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

fun MovieResponse.toData(): MovieData {
    return MovieData(
        id = id,
        name = name,
        posterImageUrl = posterImageUrl,
        originalLanguage = originalLanguage,
        releaseDate = extractDateFromString(releaseDate),
        backdropImageUrl = backDropImageUrl,
        plot = plot,
        voteCount = voteCount, voteAverage = voteAverage
    )
}