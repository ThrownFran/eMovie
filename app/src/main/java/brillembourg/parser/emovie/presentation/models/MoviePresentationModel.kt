package brillembourg.parser.emovie.presentation.models

import android.os.Parcelable
import brillembourg.parser.emovie.core.Logger
import brillembourg.parser.emovie.data.network_imp.IMAGE_PATH
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.presentation.utils.ImageType
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

fun MoviePresentationModel.getPosterCompleteUrl (): String {
    if(posterImageUrl == null) return "".also { Logger.error(IllegalArgumentException("Poster image is null")) }
    return IMAGE_PATH + posterImageUrl.replace("original","w185")
}

fun MoviePresentationModel.getBackdropCompleteUrl (): String {
    if(backdropImageUrl == null) return "".also { Logger.error(IllegalArgumentException("Backdrop image is null")) }
    return IMAGE_PATH + backdropImageUrl.replace("original","w300")
}


fun Movie.toPresentation(): MoviePresentationModel {
    return MoviePresentationModel(id, name, posterImageUrl, originalLanguage, releaseYear,backdropImageUrl,plot,voteCount,voteAverage)
}