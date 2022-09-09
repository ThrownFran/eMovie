package brillembourg.parser.emovie.data.local

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Category
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun getMovies(category: Category): Flow<List<MovieData>>
    suspend fun saveMovies(category: Category, moviesFetched: List<MovieData>)
}
