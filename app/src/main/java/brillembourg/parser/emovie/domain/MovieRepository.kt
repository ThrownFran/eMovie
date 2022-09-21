package brillembourg.parser.emovie.domain

import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.models.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(category: Category): Flow<List<Movie>>
    fun getMovie(id: Long): Flow<MovieDetail>
    suspend fun refreshMovies (category: Category)
    suspend fun requestNextMoviePage(category: Category, lastVisibleItem: Int)
    suspend fun refreshMovieDetail (id: Long)
}