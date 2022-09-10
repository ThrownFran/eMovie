package brillembourg.parser.emovie.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/top_rated/")
    suspend fun getTopRated(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
    ): GetMoviesResponse

    @GET("movie/upcoming/")
    suspend fun getUpcoming(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): GetMoviesResponse

    @GET("movie/{movie_id}/videos/")
    fun getTrailers(
        @Path("movie_id") movieId: Long,
        @Query("api_key") apiKey: String?
    ): GetTrailersResponse

}

