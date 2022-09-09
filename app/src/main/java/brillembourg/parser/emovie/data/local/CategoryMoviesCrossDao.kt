package brillembourg.parser.emovie.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryMoviesCrossDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createMovieCrossCategory(noteCrossRef: CategoryMovieCrossRef)

    @Delete
    abstract suspend fun deleteMovieCrossCategory(noteCrossRef: CategoryMovieCrossRef)

    @Transaction
    @Query("SELECT * FROM categorytable WHERE category_key = :categoryKey")
    fun getCategoryWithMovies(categoryKey: String): Flow<CategoryWithMovies>

}