package brillembourg.parser.emovie.domain

import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.models.MovieDetail
import brillembourg.parser.emovie.domain.use_cases.RefreshMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RequestNextMoviePageUseCase
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(category: Category): Flow<List<Movie>>
    fun getMovie(id: Long): Flow<MovieDetail>
    suspend fun refreshMovies (category: Category, invalidateCache: Boolean = true): RefreshMoviesUseCase.Result
    suspend fun requestNextMoviePage(category: Category, lastVisibleItem: Int): RequestNextMoviePageUseCase.Result
    suspend fun refreshMovieDetail (id: Long)


}