package brillembourg.parser.emovie.data.network_imp

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.NetworkDataSource
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.core.Logger
import brillembourg.parser.emovie.data.network_imp.responses.toData
import javax.inject.Inject

class RetrofitNetworkDataSource @Inject constructor(
    private val movieApi: MovieApi
) : NetworkDataSource {

    override suspend fun getMovies(category: Category): List<MovieData> {
        val response = when (category) {
            is Category.TopRated -> movieApi.getTopRated(API_KEY, 1)
            is Category.Upcoming -> movieApi.getUpcoming(API_KEY, 1)
        }
        return response.results?.map { it.toData() }
            ?: emptyList<MovieData>().also { Logger.error(IllegalStateException("Api results are null")) }
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