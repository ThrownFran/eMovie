package brillembourg.parser.emovie.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import brillembourg.parser.emovie.data.local.categories.CategoryDao
import brillembourg.parser.emovie.data.local.categories.CategoryTable
import brillembourg.parser.emovie.data.local.category_movie_cross.CategoryMovieCrossRef
import brillembourg.parser.emovie.data.local.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local.movies.DateConverter
import brillembourg.parser.emovie.data.local.movies.MovieDao
import brillembourg.parser.emovie.data.local.movies.MovieTable
import brillembourg.parser.emovie.data.local.trailers.TrailerDao
import brillembourg.parser.emovie.domain.models.Category
import java.util.concurrent.Executors

@Database(
    entities = arrayOf(
        MovieTable::class,
        CategoryTable::class,
        CategoryMovieCrossRef::class
    ), version = 6
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
    abstract fun trailerDao(): TrailerDao
    abstract fun movieCategoryCrossDao(): CategoryMoviesCrossDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "emovie_database"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    //pre-populate data
                    Executors.newSingleThreadExecutor().execute {
                        instance?.let {
                            it.categoryDao().saveCategory(CategoryTable(Category.Upcoming().key))
                            it.categoryDao().saveCategory(CategoryTable(Category.TopRated().key))
                        }
                    }
                }
            })
            .build()
    }
}