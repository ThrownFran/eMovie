package brillembourg.parser.emovie.data.local_imp

import androidx.room.withTransaction
import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.LocalDataSource
import brillembourg.parser.emovie.data.MoviePage
import brillembourg.parser.emovie.data.local_imp.categories.CategoryDao
import brillembourg.parser.emovie.data.local_imp.categories.CategoryTable
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
    private val categoryDao: CategoryDao,
    private val remoteKeyDao: RemoteKeyDao,
) : LocalDataSource {

    override suspend fun prepopulateCategories() {
        categoryDao.save(CategoryTable(Category.Upcoming.key))
        categoryDao.save(CategoryTable(Category.TopRated.key))
    }

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
        return crossDao.getCategoryWithMovies(category.key)
            .map { categoryWithMovies ->
                categoryWithMovies?.movies
                    ?.map { movieTable -> movieTable.toData() }
                    ?.sortedBy {
                        val remoteKey = remoteKeyDao.getRemoteKeyForMovie(
                            movieId = it.id,
                            categoryKey = categoryWithMovies.category.name
                        )
                        remoteKey?.order
                    }
                    ?: emptyList()
            }
    }

    override suspend fun saveMovies(
        category: Category,
        moviePage: MoviePage,
        nextOrder: Int,
    ) {
        saveMovies(moviePage, category, nextOrder)
    }

    override suspend fun getLastPageForCategory(category: Category): Int {
        return remoteKeyDao.getRemoteKeysForCategory(category.key).first().lastPage
    }

    override suspend fun deleteMovies(category: Category) {
        val movies = crossDao.getCategoryWithMovies(category.key).first()?.movies
        movies?.forEach { movieTable ->
            appDatabase.withTransaction {
                crossDao.delete(CategoryMovieCrossRef(
                    category.key,
                    movieTable.id
                ))
            }
        }

//        movieDao.deleteAll()
    }

    private suspend fun saveMovies(
        moviePage: MoviePage,
        category: Category,
        nextOrder: Int,
    ) {
        appDatabase.withTransaction {

            val remoteKeys = moviePage.movies.mapIndexed { i, movie ->
                RemoteKey(
                    movieId = movie.id,
                    currentPage = moviePage.currentPage,
                    lastPage = moviePage.lastPage,
                    order = nextOrder + i,
                    categoryKey = category.key
                )
            }

            val categoryMoviesCrossList = moviePage.movies.map { movie ->
                CategoryMovieCrossRef(category.key, movie.id)
            }

            movieDao.saveMovies(moviePage.movies.map { it.toTable() })
            crossDao.insertList(categoryMoviesCrossList)
            remoteKeyDao.saveRemoteKeys(remoteKeys)

//            moviePageResponse.movies.forEachIndexed { i, movieData ->
//                movieDao.saveMovie(movieData.toTable())
//                crossDao.create(CategoryMovieCrossRef(category.key, movieData.id))
//                remoteKeyDao.saveRemoteKey(RemoteKey(
//                    movieId = movieData.id,
//                    currentPage = moviePageResponse.currentPage,
//                    lastPage = moviePageResponse.lastPage,
//                    order = nextOrder + i,
//                    categoryKey = category.key
//                ))
//            }
        }
    }
}