package brillembourg.parser.emovie.data.local

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun getMovies(category: Category): Flow<List<MovieData>>
    fun getMovie(id: Long): Flow<MovieData>
    fun getTrailers(movieId: Long): Flow<List<Trailer>>
    suspend fun saveTrailers(trailers: List<Trailer>)
    suspend fun saveMovies(category: Category, moviesFetched: List<MovieData>)
}
