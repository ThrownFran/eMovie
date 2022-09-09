package brillembourg.parser.emovie.data.local

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RoomDataSource(val movieDao: MovieDao) : MovieLocalDataSource {

    var movies: List<MovieData> = listOf(
        MovieData(1L, "Movie 1", ""),
        MovieData(2L, "Movie 2", ""),
        MovieData(3L, "Movie 3", ""),
        MovieData(4L, "Movie 4", ""),
        MovieData(5L, "Movie 5", ""),
    )

    override fun getMovies(category: Category): Flow<List<MovieData>> {
        return movieDao.getList().map { list ->
            list.map { movieTable ->
                MovieData(
                    movieTable.id,
                    movieTable.name,
                    movieTable.posterImageUrl
                )
            }
        }
//        return flow { emit(movies) }
    }

    override suspend fun saveMovies(moviesFetched: List<MovieData>) {
        val movieTableList = moviesFetched.map { it.toTable() }
        movieDao.saveMovies(ArrayList(movieTableList))
//        movies = moviesFetched + moviesFetched
    }
}