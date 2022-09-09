package brillembourg.parser.emovie.data.local

import androidx.room.withTransaction
import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.local.categories.CategoryDao
import brillembourg.parser.emovie.data.local.category_movie_cross.CategoryMovieCrossRef
import brillembourg.parser.emovie.data.local.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local.movies.MovieDao
import brillembourg.parser.emovie.data.local.movies.MovieTable
import brillembourg.parser.emovie.data.local.movies.toData
import brillembourg.parser.emovie.data.local.movies.toTable
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

    override suspend fun saveMovies(category: Category, moviesToSave: List<MovieData>) {
        val movieTableList = moviesToSave.map { it.toTable() }
        saveMovies(movieTableList, category)
    }

    private suspend fun saveMovies(
        movieTableList: List<MovieTable>,
        category: Category
    ) {
        appDatabase.withTransaction {
            movieTableList.forEach {
                movieDao.saveMovie(it)
                crossDao.createMovieCrossCategory(CategoryMovieCrossRef(category.key, it.id))
            }
        }
    }
}