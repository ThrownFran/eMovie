package brillembourg.parser.emovie.data.local_imp.remote_keys

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RemoteKey(
    @PrimaryKey val movieId: Long,
    val categoryKey: String,
    val order: Int,
    val currentPage: Int,
    val lastPage: Int
)