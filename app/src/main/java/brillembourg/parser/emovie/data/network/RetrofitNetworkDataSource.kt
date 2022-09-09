package brillembourg.parser.emovie.data.network

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.toData
import brillembourg.parser.emovie.domain.Category
import javax.inject.Inject

class RetrofitNetworkDataSource @Inject constructor(private val movieApi: MovieApi) : MovieNetworkDataSource {

    override suspend fun getMovies(category: Category): List<MovieData> {
        val response = when (category) {
            is Category.TopRated -> movieApi.getTopRated(API_KEY, 1)
            is Category.Upcoming -> movieApi.getUpcoming(API_KEY, 1)
        }
        return response.results?.map { it.toData() }?: emptyList()
//        return listOf(
//            MovieData(1L,"Movie 1","Movie 1"),
//            MovieData(2L,"Movie 2","Movie 2"),
//            MovieData(3L,"Movie 3","Movie 3"),
//            MovieData(4L,"Movie 4","Movie 4"),
//            MovieData(5L,"Movie 5","Movie 5"),
//            MovieData(6L,"Movie 6","Movie 6"),
//            MovieData(7L,"Movie 7","Movie 7"),
//            MovieData(8L,"Movie 8","Movie 8"),
//        )
    }
}