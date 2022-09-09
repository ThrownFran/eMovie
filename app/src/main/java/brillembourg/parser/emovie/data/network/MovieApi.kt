package brillembourg.parser.emovie.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/top_rated/")
    suspend fun getTopRated(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/upcoming/")
    suspend fun getUpcoming(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieListResponse

}

