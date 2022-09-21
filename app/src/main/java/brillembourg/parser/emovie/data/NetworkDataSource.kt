package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer

interface NetworkDataSource {
    suspend fun getMovies(category: Category, page: Int = 1): List<MovieData>
    suspend fun getTrailers(movieId: Long): List<Trailer>
}
