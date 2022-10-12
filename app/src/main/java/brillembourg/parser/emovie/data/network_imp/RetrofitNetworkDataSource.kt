package brillembourg.parser.emovie.data.network_imp

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.NetworkDataSource
import brillembourg.parser.emovie.data.toData
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.core.Logger
import javax.inject.Inject

class RetrofitNetworkDataSource @Inject constructor(
    private val movieApi: MovieApi
) : NetworkDataSource {

    override suspend fun getMovies(category: Category, page: Int): NetworkDataSource.MoviePageResponse {
        val response = when (category) {
            is Category.TopRated -> movieApi.getTopRated(API_KEY, page)
            is Category.Upcoming -> movieApi.getUpcoming(API_KEY, page)
        }

        return NetworkDataSource.MoviePageResponse(
            movies = response.results?.map { it.toData() }
                ?: emptyList<MovieData>().also { Logger.error(IllegalStateException("Api results are null")) },
            currentPage = response.currentPage,
            lastPage = response.lastPage
        )

//        return response.results?.map { it.toData() }
//            ?: emptyList<MovieData>().also { Logger.error(IllegalStateException("Api results are null")) }
    }

    override suspend fun getTrailers(movieId: Long): List<Trailer> {
        val response = movieApi.getTrailers(movieId, API_KEY)
        return response.results.map { trailerResponse ->
            Trailer(
                trailerResponse.id,
                trailerResponse.key,
                trailerResponse.name,
                trailerResponse.site,
                movieId
            )
        }
    }
}