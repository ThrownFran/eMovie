package brillembourg.parser.emovie.domain

import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(category: Category): Flow<List<Movie>>
    suspend fun refreshData (category: Category)
}