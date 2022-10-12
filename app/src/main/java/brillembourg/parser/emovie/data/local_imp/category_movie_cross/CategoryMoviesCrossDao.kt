package brillembourg.parser.emovie.data.local_imp.category_movie_cross

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryMoviesCrossDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create (noteCrossRef: CategoryMovieCrossRef)

    @Delete
    suspend fun delete(noteCrossRef: CategoryMovieCrossRef)

    @Transaction
    @Query("SELECT * FROM categorytable")
    fun getCategoriesWithMovies(): Flow<List<CategoryWithMovies>>

    @Transaction
    @Query("SELECT * FROM categorytable WHERE category_key = :categoryKey")
    fun getCategoryWithMovies(categoryKey: String): Flow<CategoryWithMovies>

}