package brillembourg.parser.emovie.data.local.movies

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovies(movies: ArrayList<MovieTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovie(movie: MovieTable)

    @Query("SELECT * FROM movietable")
    fun getList(): Flow<List<MovieTable>>

}