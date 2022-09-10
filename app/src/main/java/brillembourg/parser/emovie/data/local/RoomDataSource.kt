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
import brillembourg.parser.emovie.data.local.trailers.TrailerDao
import brillembourg.parser.emovie.data.local.trailers.TrailerTable
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    val appDatabase: AppDatabase,
    val movieDao: MovieDao,
    val categoryDao: CategoryDao,
    val trailerDao: TrailerDao,
    val crossDao: CategoryMoviesCrossDao
) : MovieLocalDataSource {

    override fun getMovie(id: Long): Flow<MovieData> {
        return movieDao.getMovie(id).map { it.toData() }
    }

    override fun getTrailers(movieId: Long): Flow<List<Trailer>> {
        return trailerDao.getMovieWithTrailers(movieId).map {
            it.trailers.map { trailerTable ->
                Trailer(
                    trailerTable.id,
                    trailerTable.key,
                    trailerTable.name,
                    trailerTable.site,
                    movieId
                )
            }
        }
    }

    override suspend fun saveTrailers(trailers: List<Trailer>) {
        trailerDao.saveTrailers(ArrayList(trailers.map {
            TrailerTable(
                it.id,
                it.name,
                it.key,
                it.site,
                it.movieId
            )
        }))
    }

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