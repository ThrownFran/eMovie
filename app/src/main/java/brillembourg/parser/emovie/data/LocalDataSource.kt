package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun prepopulateCategories();
    fun getMovies(category: Category): Flow<List<MovieData>>
    fun getMovie(id: Long): Flow<MovieData>
    fun getTrailers(movieId: Long): Flow<List<Trailer>>
    suspend fun saveTrailers(trailers: List<Trailer>)
    suspend fun saveMovies(
        category: Category,
        moviePage: MoviePage,
        nextOrder: Int
    )

    suspend fun deleteMovies(category: Category)
    suspend fun getLastPageForCategory(category: Category): Int
}
