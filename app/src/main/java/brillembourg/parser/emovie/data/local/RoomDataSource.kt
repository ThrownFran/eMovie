package brillembourg.parser.emovie.data.local

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.domain.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    val movieDao: MovieDao,
    val categoryDao: CategoryDao,
    val crossDao: CategoryMoviesCrossDao
) : MovieLocalDataSource {

    var movies: List<MovieData> = listOf(
        MovieData(1L, "Movie 1", ""),
        MovieData(2L, "Movie 2", ""),
        MovieData(3L, "Movie 3", ""),
        MovieData(4L, "Movie 4", ""),
        MovieData(5L, "Movie 5", ""),
    )

    override fun getMovies(category: Category): Flow<List<MovieData>> {

        return crossDao.getCategoryWithMovies(category.key)
            .transform { try {
                emit(it.movies.map { table -> table.toData() })
            } catch (e: Exception) {
                emit(emptyList())
            } }

//        return movieDao.getList().map { list ->
//            list.map { movieTable ->
//                MovieData(
//                    movieTable.id,
//                    movieTable.name,
//                    movieTable.posterImageUrl
//                )
//            }
//        }
//        return flow { emit(movies) }
    }

    override suspend fun saveMovies(category: Category, moviesFetched: List<MovieData>) {
        val movieTableList = moviesFetched.map { it.toTable() }
        movieTableList.forEach {
            movieDao.saveMovie(it)
            crossDao.createMovieCrossCategory(CategoryMovieCrossRef(category.key,it.id))
        }
    }
}