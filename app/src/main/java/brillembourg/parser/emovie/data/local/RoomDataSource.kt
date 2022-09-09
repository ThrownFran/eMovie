package brillembourg.parser.emovie.data.local

import android.util.Log
import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Category
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    val movieDao: MovieDao,
    val categoryDao: CategoryDao,
    val crossDao: CategoryMoviesCrossDao
) : MovieLocalDataSource {

    var movies: List<MovieData> = listOf(
        MovieData(1L, "Movie 1", ""),
        MovieData(2L, "Movie 2", ""),
        MovieData(3L, "Movie 3", ""),
        MovieData(4L, "Movie 4", ""),
        MovieData(5L, "Movie 5", ""),
    )

    override fun getMovies(category: Category): Flow<List<MovieData>> {
        return crossDao.getCategoriesWithMovies()
            .transform { categories ->
                categories.forEach {
                    if (it.category.name == category.key) {
                        emit(it)
                    }
                }
            }.transform {
                try {
                    emit(it.movies.map { table -> table.toData() })
                } catch (e: Exception) {
                    emit(emptyList())
                }
            }
    }

    override suspend fun saveMovies(category: Category, moviesFetched: List<MovieData>) {
        val movieTableList = moviesFetched.map { it.toTable() }
        movieTableList.forEach {
            movieDao.saveMovie(it)
            crossDao.createMovieCrossCategory(CategoryMovieCrossRef(category.key, it.id))
        }
    }
}