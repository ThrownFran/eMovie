package brillembourg.parser.emovie.data.local.movies

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import brillembourg.parser.emovie.data.MovieData
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

@Entity
data class MovieTable(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "poster_image_url") val posterImageUrl: String? = null,
    @ColumnInfo(name = "original_language") val originalLanguage: String,
    @ColumnInfo(name = "release_date") val releaseDate: LocalDate,
    @ColumnInfo(name = "backdrop_image_url") val backdropImageUrl: String?,
    @ColumnInfo(name = "plot") val plot: String,
    @ColumnInfo(name = "vote_count") val voteCount: Int,
    @ColumnInfo(name = "vote_average") val voteAverage: Float
)

fun MovieData.toTable () : MovieTable {
    return MovieTable(id,name, posterImageUrl, originalLanguage, releaseDate,backdropImageUrl,plot,voteCount,voteAverage)
}

fun MovieTable.toData () : MovieData {
    return MovieData(id, name, posterImageUrl, originalLanguage, releaseDate, backdropImageUrl,plot,voteCount,voteAverage)
}

object DateConverter {

    @TypeConverter
    fun toDate(epochSeconds: Long): LocalDate {
        return Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @TypeConverter
    fun fromDate(date: LocalDate): Long {
        val zoneId: ZoneId = ZoneId.systemDefault()
        val epoch = date.atStartOfDay(zoneId).toEpochSecond()
        return epoch
    }
}