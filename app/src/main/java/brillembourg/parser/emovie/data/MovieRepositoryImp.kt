package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.data.local.MovieLocalDataSource
import brillembourg.parser.emovie.data.network.MovieNetworkDataSource
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MovieRepositoryImp @Inject constructor(
    private val schedulers: Schedulers,
    private val localDataSource: MovieLocalDataSource,
    private val networkDataSource: MovieNetworkDataSource
) : MovieRepository {

    override suspend fun refreshData(category: Category) {
        try {
            val moviesFromLocal = localDataSource.getMovies(category).first()
            val moviesFromNetwork = networkDataSource.getMovies(category)
            if (moviesFromLocal != moviesFromNetwork) {
                localDataSource.saveMovies(moviesFromNetwork)
            }
        } catch (e: Exception) {
            throw (e.toDomain())
        }
    }

    override fun getMovies(category: Category): Flow<List<Movie>> {
        return localDataSource.getMovies(category)
            .catch { if(it is Exception) throw it.toDomain() else throw GenericException("Throwable: ${it.message}") }
            .flowOn(schedulers.ioDispatcher())
            .map { list -> list.map { movieData -> movieData.toDomain() } }
    }

}