package brillembourg.parser.emovie.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import brillembourg.parser.emovie.domain.Category
import java.util.concurrent.Executors

@Database(
    entities = arrayOf(
        MovieTable::class,
        CategoryTable::class,
        CategoryMovieCrossRef::class
    ), version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
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