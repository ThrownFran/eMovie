package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class MovieRepositoryImp(
    private val schedulers: Schedulers,
    private val localDataSource: MovieLocalDataSource,
    private val networkDataSource: MovieNetworkDataSource
) : MovieRepository {

    override suspend fun getMovies(category: Category): Flow<List<Movie>> {
        return localDataSource.getMovies(category)
            .flowOn(schedulers.ioDispatcher())
            .map { list -> list.map { movieData -> movieData.toDomain() } }
            .onEach {
                val moviesFetched = networkDataSource.getMovies(category)
                localDataSource.saveMovies(moviesFetched)
            }
    }
}