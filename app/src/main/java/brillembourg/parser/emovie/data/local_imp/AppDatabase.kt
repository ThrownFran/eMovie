package brillembourg.parser.emovie.data.local_imp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import brillembourg.parser.emovie.data.local_imp.categories.CategoryDao
import brillembourg.parser.emovie.data.local_imp.categories.CategoryTable
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMovieCrossRef
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieTable
import brillembourg.parser.emovie.data.local_imp.remote_keys.RemoteKey
import brillembourg.parser.emovie.data.local_imp.remote_keys.RemoteKeyDao
import brillembourg.parser.emovie.data.local_imp.trailers.TrailerDao
import brillembourg.parser.emovie.data.local_imp.trailers.TrailerTable
import brillembourg.parser.emovie.domain.models.Category
import java.util.concurrent.Executors

@Database(
    entities = [MovieTable::class,
        CategoryTable::class,
        CategoryMovieCrossRef::class,
        RemoteKey::class,
        TrailerTable::class], version = 3
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
    abstract fun trailerDao(): TrailerDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun movieCategoryCrossDao(): CategoryMoviesCrossDao

    companion object {
        private const val databaseName = "emovie_database"

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                databaseName
            )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        prepopulateCategories()
                    }
                })
                .build()
        }

        private fun prepopulateCategories() {
            Executors.newSingleThreadExecutor().execute {
                instance?.let {
                    it.categoryDao().save(CategoryTable(Category.Upcoming().key))
                    it.categoryDao().save(CategoryTable(Category.TopRated().key))
                }
            }
        }
    }
}