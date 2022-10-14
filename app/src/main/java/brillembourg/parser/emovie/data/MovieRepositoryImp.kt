package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.core.GenericException
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.core.Schedulers
import brillembourg.parser.emovie.domain.models.MovieDetail
import brillembourg.parser.emovie.domain.use_cases.RefreshMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RequestNextMoviePageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal const val PAGE_SIZE = 20
internal const val PAGE_THRESHOLD = 10

class MovieRepositoryImp @Inject constructor(
    private val schedulers: Schedulers,
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) : MovieRepository {

    init {
        CoroutineScope(schedulers.ioDispatcher()).launch {
            localDataSource.prepopulateCategories()
        }
    }

    override suspend fun requestNextMoviePage(category: Category, lastVisibleItem: Int): RequestNextMoviePageUseCase.Result {

        try {
            val size = localDataSource.getMovies(category).first().size
            val isLastItemReached = lastVisibleItem >= size - 1
            if(isLastItemReached) {
                val nextPage = size / PAGE_SIZE + 1
                val lastPage = localDataSource.getLastPageForCategory(category)

                if(nextPage > lastPage) {
                    return RequestNextMoviePageUseCase.Result.LastPageAlreadyReached
                }

                val moviePagedResponse = networkDataSource.getMovies(category,nextPage)
                localDataSource.saveMovies(category, moviePagedResponse, size)
                return RequestNextMoviePageUseCase.Result.RequestSuccess
            } else {
                return RequestNextMoviePageUseCase.Result.LastItemInPageNotReachedYet
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

    override suspend fun refreshMovies(category: Category, invalidateCache: Boolean) : RefreshMoviesUseCase.Result{
        try {
            val moviesFromLocal = localDataSource.getMovies(category).first()
            val moviesResponseFromNetwork = networkDataSource.getMovies(category)

            if(invalidateCache) localDataSource.deleteMovies(category)

            localDataSource.saveMovies(category, moviesResponseFromNetwork, localDataSource.getMovies(category).first().size)

            if(moviesResponseFromNetwork.currentPage == moviesResponseFromNetwork.lastPage) {
                return RefreshMoviesUseCase.Result.IsFirstAndLastPage
            } else {
                return RefreshMoviesUseCase.Result.HasMorePagesToLoad
            }

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