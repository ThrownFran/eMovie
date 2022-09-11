package brillembourg.parser.emovie.data.local.trailers

import androidx.room.*
import brillembourg.parser.emovie.data.local.movies.MovieTable
import brillembourg.parser.emovie.domain.models.Trailer

@Entity
data class TrailerTable(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "site") val site: String,
    @ColumnInfo(name = "movie_id") val movieId: Long,
)

fun TrailerTable.toDomain () : Trailer {
    return Trailer(id = id, key = key, name = name, site = site, movieId = movieId)
}

fun Trailer.toTable () : TrailerTable {
    return TrailerTable(id = id, name = name, key = key, site = site, movieId = movieId)
}

data class MovieWithTrailers(
    @Embedded val movie: MovieTable,
    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id"
    )
    val trailers: List<TrailerTable>
)

