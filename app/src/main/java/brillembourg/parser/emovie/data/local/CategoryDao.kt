package brillembourg.parser.emovie.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategory(category: CategoryTable)



}