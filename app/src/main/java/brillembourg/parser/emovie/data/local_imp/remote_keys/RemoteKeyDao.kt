package brillembourg.parser.emovie.data.local_imp.remote_keys

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRemoteKey(remoteKey: RemoteKey)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRemoteKeys(remoteKeys: List<RemoteKey>)

    @Query("SELECT * FROM remotekey WHERE movieId = :movieId AND categoryKey = :categoryKey")
    fun getRemoteKeyForMovie(movieId: Long, categoryKey: String): RemoteKey?

    @Query("SELECT * FROM remotekey WHERE categoryKey = :categoryKey")
    fun getRemoteKeysForCategory(categoryKey: String): List<RemoteKey>

}