package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.data.network.MovieResponse
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.presentation.utils.extractDateFromString
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

data class MovieData(
    val id: Long,
    val name: String,
    val posterImageUrl: String? = null,
    val originalLanguage: String,
    val releaseDate: LocalDate,
    val backdropImageUrl: String?,
    val plot: String,
    val voteCount: Int,
    val voteAverage: Float
)

fun MovieData.toDomain(): Movie {
    return Movie(
        id = id,
        name = name,
        posterImageUrl = posterImageUrl,
        originalLanguage = originalLanguage,
        releaseYear = releaseDate,
        backdropImageUrl = backdropImageUrl,
        plot = plot,
        voteCount = voteCount,
        voteAverage = voteAverage
    )
}

fun Movie.toData(): MovieData {
    return MovieData(
        id = id,
        name = name,
        posterImageUrl = posterImageUrl,
        originalLanguage = originalLanguage,
        releaseDate = releaseYear,
        backdropImageUrl = backdropImageUrl,
        plot = plot,
        voteCount = voteCount,
        voteAverage = voteAverage
    )
}

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