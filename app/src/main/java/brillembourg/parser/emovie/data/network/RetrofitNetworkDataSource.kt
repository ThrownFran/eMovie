package brillembourg.parser.emovie.data.network

import brillembourg.parser.emovie.data.MovieData
import brillembourg.parser.emovie.data.toData
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.presentation.utils.Logger
import javax.inject.Inject

class RetrofitNetworkDataSource @Inject constructor(
    private val movieApi: MovieApi
) : MovieNetworkDataSource {

    override suspend fun getMovies(category: Category): List<MovieData> {
        val response = when (category) {
            is Category.TopRated -> movieApi.getTopRated(API_KEY, 1)
            is Category.Upcoming -> movieApi.getUpcoming(API_KEY, 1)
        }
        return response.results?.map { it.toData() }
            ?: emptyList<MovieData>().also { Logger.error(IllegalStateException("Api results are null")) }
    }

    override suspend fun getTrailers(id: Long): List<Trailer> {
        TODO("Not yet implemented")
    }
}