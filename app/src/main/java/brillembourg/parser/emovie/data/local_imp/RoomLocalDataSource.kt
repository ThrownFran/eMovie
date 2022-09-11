package brillembourg.parser.emovie.data.local_imp

import androidx.room.withTransaction
import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.LocalDataSource
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMovieCrossRef
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieTable
import brillembourg.parser.emovie.data.local_imp.movies.toData
import brillembourg.parser.emovie.data.local_imp.movies.toTable
import brillembourg.parser.emovie.data.local_imp.trailers.TrailerDao
import brillembourg.parser.emovie.data.local_imp.trailers.toDomain
import brillembourg.parser.emovie.data.local_imp.trailers.toTable
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RoomLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val movieDao: MovieDao,
    private val trailerDao: TrailerDao,
    private val crossDao: CategoryMoviesCrossDao
) : LocalDataSource {

    override fun getMovie(id: Long): Flow<MovieData> {
        return movieDao.getMovie(id).map { movieTable -> movieTable.toData() }
    }

    override fun getTrailers(movieId: Long): Flow<List<Trailer>> {
        return trailerDao.getMovieWithTrailers(movieId).map {
            it.trailers.map { trailerTable ->
                trailerTable.toDomain()
            }
        }
    }

    override suspend fun saveTrailers(trailers: List<Trailer>) {
        trailerDao.saveTrailers(ArrayList(trailers.map { trailer ->
            trailer.toTable()
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
            movieTableList.forEach { movieTable ->
                movieDao.saveMovie(movieTable)
                crossDao.create(CategoryMovieCrossRef(category.key, movieTable.id))
            }
        }
    }
}