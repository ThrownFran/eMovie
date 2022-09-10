package brillembourg.parser.emovie.data.local.trailers

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrailerDao {

    @Transaction
    @Query("SELECT * FROM movietable WHERE id = :movieId")
    fun getMovieWithTrailers(movieId: Long): Flow<MovieWithTrailers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrailers(trailers: ArrayList<TrailerTable>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveMovie(movie: MovieTable)
//
//    @Query("SELECT * FROM movietable")
//    fun getList(): Flow<List<MovieTable>>
//
//    @Query("SELECT * FROM movietable WHERE id = :id")
//    fun getMovie(id: Long): Flow<MovieTable>

}