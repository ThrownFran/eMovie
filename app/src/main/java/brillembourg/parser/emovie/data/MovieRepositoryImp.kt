package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.core.GenericException
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.core.Schedulers
import brillembourg.parser.emovie.domain.models.MovieDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

internal const val PAGE_SIZE = 20
internal const val PAGE_THRESHOLD = 10

class MovieRepositoryImp @Inject constructor(
    private val schedulers: Schedulers,
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) : MovieRepository {

    override suspend fun requestNextMoviePage(category: Category, lastVisibleItem: Int) {

        try {
            val size = localDataSource.getMovies(category).first().size
            val isLastItemReached = lastVisibleItem >= size - 1
            if(isLastItemReached) {
                val page = size / PAGE_SIZE + 1
                val moviePagedResponse = networkDataSource.getMovies(category,page)
                localDataSource.saveMovies(category, moviePagedResponse, size)
            }

        } catch (e: Exception) {
            throw (e.toDomain())
        }

    }

    override fun getMovies(category: Category): Flow<List<Movie>> {
        return localDataSource.getMovies(category)
            .catch { if (it is Exception) throw it.toDomain() else throw GenericException("Throwable: ${it.message}") }
            .flowOn(schedulers.ioDispatcher())
            .map { list -> list.map { movieData -> movieData.toDomain() } }
    }

    override fun getMovie(id: Long): Flow<MovieDetail> {
        return localDataSource.getMovie(id)
            .combine(localDataSource.getTrailers(id)) { movieData, trailer ->
                MovieDetail(movieData.toDomain(), trailer)
            }
            .catch { if (it is Exception) throw it.toDomain() else throw GenericException("Throwable: ${it.message}") }
            .flowOn(schedulers.ioDispatcher())
    }

    override suspend fun refreshMovies(category: Category, invalidateCache: Boolean) {
        try {
            val moviesFromLocal = localDataSource.getMovies(category).first()
            val moviesFromNetwork = networkDataSource.getMovies(category)
            if(invalidateCache) localDataSource.deleteMovies(category)
            localDataSource.saveMovies(category, moviesFromNetwork, moviesFromLocal.size)
        } catch (e: Exception) {
            throw (e.toDomain())
        }
    }

    override suspend fun refreshMovieDetail(id: Long) {
        try {
            val trailersFromLocal = localDataSource.getTrailers(id).first()
            val trailersFromNetwork = networkDataSource.getTrailers(id)
            if (trailersFromLocal != trailersFromNetwork) {
                localDataSource.saveTrailers(trailersFromNetwork)
            }
        } catch (e: Exception) {
            throw (e.toDomain())
        }
    }


}