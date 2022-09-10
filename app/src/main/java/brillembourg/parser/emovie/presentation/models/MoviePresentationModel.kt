package brillembourg.parser.emovie.presentation.models

import android.os.Parcelable
import brillembourg.parser.emovie.domain.models.Movie
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class MoviePresentationModel(
    val id: Long,
    val name: String,
    val posterImageUrl: String? = null,
    val originalLanguage: String,
    val releaseDate: LocalDate,
    val backdropImageUrl: String?,
    val plot: String,
    val voteCount: Int,
    val voteAverage: Float
) : Parcelable {

    fun getReleaseYear (): Int {
        return releaseDate.year
    }

}

fun MoviePresentationModel.toDomain(): Movie {
    return Movie(id, name, posterImageUrl, originalLanguage, releaseDate,backdropImageUrl,plot,voteCount,voteAverage)
}

fun Movie.toPresentation(): MoviePresentationModel {
    return MoviePresentationModel(id, name, posterImageUrl, originalLanguage, releaseYear,backdropImageUrl,plot,voteCount,voteAverage)
}