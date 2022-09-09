package brillembourg.parser.emovie.data.network

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Category

interface MovieNetworkDataSource {
    suspend fun getMovies(category: Category): List<MovieData>
}
