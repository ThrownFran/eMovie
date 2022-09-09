package brillembourg.parser.emovie.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import brillembourg.parser.emovie.domain.Category

@Entity(tableName = "categorytable")
data class CategoryTable(
    @PrimaryKey @ColumnInfo(name = "category_key") val name: String
)

fun CategoryTable.toDomain(): Category {
    return when (name) {
        Category.Upcoming().key -> Category.Upcoming()
        Category.TopRated().key -> Category.TopRated()
        else -> throw IllegalArgumentException("Category name not supported: $name")
    }
}

fun Category.toTable(): CategoryTable {
    return when (this) {
        is Category.TopRated -> CategoryTable(key)
        is Category.Upcoming -> CategoryTable(key)
    }
}