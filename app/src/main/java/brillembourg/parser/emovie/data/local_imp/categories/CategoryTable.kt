package brillembourg.parser.emovie.data.local_imp.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorytable")
data class CategoryTable(
    @PrimaryKey @ColumnInfo(name = "category_key") val name: String
)