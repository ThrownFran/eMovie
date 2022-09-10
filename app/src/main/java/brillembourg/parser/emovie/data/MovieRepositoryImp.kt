package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.data.local.MovieLocalDataSource
import brillembourg.parser.emovie.data.network.MovieNetworkDataSource
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import brillembourg.parser.emovie.domain.models.MovieDetail
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MovieRepositoryImp @Inject constructor(
    private val schedulers: Schedulers,
    private val localDataSource: MovieLocalDataSource,
    private val networkDataSource: MovieNetworkDataSource
) : MovieRepository {

    override suspend fun refreshMovies(category: Category) {
        try {
            val moviesFromLocal = localDataSource.getMovies(category).first()
            val moviesFromNetwork = networkDataSource.getMovies(category)
            if (moviesFromLocal != moviesFromNetwork) {
                localDataSource.saveMovies(category, moviesFromNetwork)
            }
        } catch (e: Exception) {
            throw (e.toDomain())
        }
    }

    override suspend fun refreshMovieDetail(id: Long) {

        try {
            val trailersFromLocal = localDataSource.getTrailers(id).first()
            val trailersFromNetwork = networkDataSource.getTrailers(id)
            if(trailersFromLocal != trailersFromNetwork) {
                localDataSource.saveTrailers(trailersFromNetwork)
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
//        val flowMovies = localDataSource.getMovie(id).map { it.toDomain() }
//        val flowTrailer = localDataSource.getTrailer(id)
//        flowOf(flowMovies,flowTrailer)
//            .transform {
//                MovieDetail(it.)
//            }

        return localDataSource.getMovie(id)
            .combine(localDataSource.getTrailers(id)
            ) { movieData, trailer ->
                MovieDetail(movieData.toDomain(), trailer)
            }
            .catch { if (it is Exception) throw it.toDomain() else throw GenericException("Throwable: ${it.message}") }
            .flowOn(schedulers.ioDispatcher())

//        return localDataSource.getMovie(id)
//            .combine(localDataSource.getTrailers(id)
//                .catch { if (it is Exception) throw it.toDomain() else throw GenericException("Throwable: ${it.message}") }
//                .map { pair ->
//                    MovieDetail(pair.toDomain(), listOf())
//                }
//                .flowOn(schedulers.ioDispatcher())
    }
}