package brillembourg.parser.emovie.data.local_imp.trailers

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrailerDao {
    @Transaction
    @Query("SELECT * FROM movietable WHERE id = :movieId")
    fun getMovieWithTrailers(movieId: Long): Flow<MovieWithTrailers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrailers(trailers: ArrayList<TrailerTable>)
}