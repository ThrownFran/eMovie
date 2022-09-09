package brillembourg.parser.emovie.data.local

import android.util.Log
import androidx.room.withTransaction
import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Category
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    val appDatabase: AppDatabase,
    val movieDao: MovieDao,
    val categoryDao: CategoryDao,
    val crossDao: CategoryMoviesCrossDao
) : MovieLocalDataSource {

    override fun getMovies(category: Category): Flow<List<MovieData>> {
        return crossDao.getCategoriesWithMovies()
            .transform { categories ->
                categories.forEach { categoryWithMovies ->
                    if (categoryWithMovies.category.name == category.key) {
                        emit(categoryWithMovies.movies.map { table -> table.toData() })
                    }
                }
            }
    }

    override suspend fun saveMovies(category: Category, moviesFetched: List<MovieData>) {
        val movieTableList = moviesFetched.map { it.toTable() }
        appDatabase.withTransaction {
            movieTableList.forEach {
                movieDao.saveMovie(it)
                crossDao.createMovieCrossCategory(CategoryMovieCrossRef(category.key, it.id))
            }
        }
    }
}