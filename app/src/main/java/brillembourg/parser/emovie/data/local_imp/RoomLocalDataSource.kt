package brillembourg.parser.emovie.data.local_imp

import androidx.room.Update
import androidx.room.withTransaction
import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.LocalDataSource
import brillembourg.parser.emovie.data.NetworkDataSource
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMovieCrossRef
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieDao
import brillembourg.parser.emovie.data.local_imp.movies.toData
import brillembourg.parser.emovie.data.local_imp.movies.toTable
import brillembourg.parser.emovie.data.local_imp.remote_keys.RemoteKey
import brillembourg.parser.emovie.data.local_imp.remote_keys.RemoteKeyDao
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
    private val crossDao: CategoryMoviesCrossDao,
    private val remoteKeyDao: RemoteKeyDao,
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
                        emit(
                            categoryWithMovies.movies.map { table ->
                                table.toData()
                            }
                                .sortedBy {
                                    val remoteKey = remoteKeyDao.getRemoteKeyForMovie(
                                        movieId = it.id,
                                        categoryKey = categoryWithMovies.category.name
                                    )
                                    //TODO remove
                                    it.name = remoteKey?.order.toString()
                                    remoteKey?.order
                                }
                        )
                    }
                }
            }
    }

    override suspend fun saveMovies(
        category: Category,
        movieResponse: NetworkDataSource.MoviePageResponse,
        nextOrder: Int,
    ) {
        saveMovies(movieResponse, category, nextOrder)
    }

    override suspend fun deleteMovies(category: Category) {
        val movies = crossDao.getCategoryWithMovies(category.key).first().movies
        movies.forEach { movieTable ->
            appDatabase.withTransaction {
                crossDao.delete(CategoryMovieCrossRef(
                    category.key,
                    movieTable.id
                ))
            }
        }
    }

    private suspend fun saveMovies(
        moviePageResponse: NetworkDataSource.MoviePageResponse,
        category: Category,
        nextOrder: Int,
    ) {
        appDatabase.withTransaction {
            moviePageResponse.movies.forEachIndexed { i, movieData ->
                movieDao.saveMovie(movieData.toTable())
                crossDao.create(CategoryMovieCrossRef(category.key, movieData.id))
                remoteKeyDao.saveRemoteKey(RemoteKey(
                    movieId = movieData.id,
                    currentPage = moviePageResponse.currentPage,
                    lastPage = moviePageResponse.lastPage,
                    order = nextOrder + i,
                    categoryKey = category.key
                ))
            }
        }
    }
}