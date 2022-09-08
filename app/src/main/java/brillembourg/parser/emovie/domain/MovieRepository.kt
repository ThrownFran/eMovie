package brillembourg.parser.emovie.domain

import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getMovies(category: Category): Flow<List<Movie>>
}