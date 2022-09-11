package brillembourg.parser.emovie.data.local_imp.categories

import androidx.room.*

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(category: CategoryTable)

}