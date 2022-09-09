package brillembourg.parser.emovie.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import brillembourg.parser.emovie.data.MovieData

@Entity
data class MovieTable(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "poster_image_url") val posterImageUrl: String,
)

fun MovieData.toTable () : MovieTable {
    return MovieTable(id,name, posterImageUrl)
}

fun MovieTable.toData () : MovieData {
    return MovieData(id,name,posterImageUrl)
}