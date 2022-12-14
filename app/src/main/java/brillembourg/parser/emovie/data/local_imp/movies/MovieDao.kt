package brillembourg.parser.emovie.data.local_imp.movies

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovie(movie: MovieTable)

    @Query("SELECT * FROM movietable")
    fun getList(): Flow<List<MovieTable>>

    @Query("SELECT * FROM movietable WHERE id = :id")
    fun getMovie(id: Long): Flow<MovieTable>

}