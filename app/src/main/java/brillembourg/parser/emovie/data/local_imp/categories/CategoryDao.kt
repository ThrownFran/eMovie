package brillembourg.parser.emovie.data.local_imp.categories

import androidx.room.*

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(category: CategoryTable)

    @Query("SELECT * FROM categorytable")
    suspend fun getCategories (): List<CategoryTable>

}