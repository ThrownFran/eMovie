package brillembourg.parser.emovie.data.local

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RoomDataSource: MovieLocalDataSource {

    var movies: List<MovieData> = listOf(
        MovieData(1L, "Movie 1",""),
        MovieData(1L, "Movie 2",""),
        MovieData(1L, "Movie 3",""),
        MovieData(1L, "Movie 4",""),
        MovieData(1L, "Movie 5",""),
    )

    override fun getMovies(category: Category): Flow<List<MovieData>> {
        return flow { emit(movies) }
    }

    override suspend fun saveMovies(moviesFetched: List<MovieData>) {
        movies = moviesFetched + moviesFetched
    }
}