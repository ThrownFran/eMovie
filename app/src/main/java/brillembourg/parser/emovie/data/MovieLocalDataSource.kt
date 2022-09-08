package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.Movie
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    suspend fun getMovies(category: Category): Flow<List<MovieData>>
    suspend fun saveMovies(moviesFetched: List<MovieData>)
}
