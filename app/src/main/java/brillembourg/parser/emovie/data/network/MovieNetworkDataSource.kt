package brillembourg.parser.emovie.data.network

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer

interface MovieNetworkDataSource {
    suspend fun getMovies(category: Category): List<MovieData>
    suspend fun getTrailers(movieId: Long): List<Trailer>
}
