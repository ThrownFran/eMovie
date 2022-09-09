package brillembourg.parser.emovie.di

import brillembourg.parser.emovie.data.network.MovieApi
import brillembourg.parser.emovie.data.network.MovieNetworkDataSource
import brillembourg.parser.emovie.data.network.RetrofitNetworkDataSource
import brillembourg.parser.emovie.data.network.SERVER_URL
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkSourceModule {

    @Singleton
    @Provides
    fun provideNetworkDataSource(movieApi: MovieApi): MovieNetworkDataSource = RetrofitNetworkDataSource(movieApi)

    @Singleton
    @Provides
    fun getMovieApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }

    @Singleton
    @Provides
    fun getRetrofit(
        httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    @Singleton
    @Provides
    fun getHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient
            .addInterceptor(logging)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .connectTimeout(8, TimeUnit.SECONDS)

        return httpClient.build()
    }

}