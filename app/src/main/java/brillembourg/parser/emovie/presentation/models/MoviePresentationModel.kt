package brillembourg.parser.emovie.presentation.models

import android.os.Parcelable
import brillembourg.parser.emovie.domain.Movie
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class MoviePresentationModel(
    val id: Long,
    val name: String,
    val posterImageUrl: String? = null,
    val originalLanguage: String,
    val releaseDate: LocalDate
) : Parcelable {

    fun getReleaseYear (): Int {
        return releaseDate.year
    }

}

fun MoviePresentationModel.toDomain(): Movie {
    return Movie(id, name, posterImageUrl, originalLanguage, releaseDate)
}

fun Movie.toPresentation(): MoviePresentationModel {
    return MoviePresentationModel(id, name, posterImageUrl, originalLanguage, releaseYear)
}