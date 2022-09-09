package brillembourg.parser.emovie.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovies(movies: ArrayList<MovieTable>)

    @Query("SELECT * FROM movietable")
    fun getList(): Flow<List<MovieTable>>

}