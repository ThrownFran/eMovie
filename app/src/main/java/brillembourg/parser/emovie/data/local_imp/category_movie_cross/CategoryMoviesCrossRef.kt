package brillembourg.parser.emovie.data.local_imp.category_movie_cross

import androidx.room.*
import brillembourg.parser.emovie.data.local_imp.categories.CategoryTable
import brillembourg.parser.emovie.data.local_imp.movies.MovieTable

@Entity(
    tableName = "category_movies_cross_ref", primaryKeys = ["category_key", "id"], foreignKeys = [
        ForeignKey(
            entity = CategoryTable::class,
            parentColumns = ["category_key"],
            childColumns = ["category_key"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieTable::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class CategoryMovieCrossRef(
    @ColumnInfo(name = "category_key") val categoryKey: String,
    @ColumnInfo(name = "id") val movieId: Long
)

data class CategoryWithMovies(
    @Embedded val category: CategoryTable,
    @Relation(
        parentColumn = "category_key",
        entityColumn = "id",
        associateBy = Junction(CategoryMovieCrossRef::class)
    )
    val movies: List<MovieTable>
)


