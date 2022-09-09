package brillembourg.parser.emovie.data.local.categories

import androidx.room.*

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategory(category: CategoryTable)



}