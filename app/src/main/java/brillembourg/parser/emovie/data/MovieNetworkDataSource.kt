package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.Movie
import kotlinx.coroutines.flow.Flow

interface MovieNetworkDataSource {
    suspend fun getMovies(category: Category): List<MovieData>
}
