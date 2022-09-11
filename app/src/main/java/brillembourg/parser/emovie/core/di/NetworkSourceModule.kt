package brillembourg.parser.emovie.core.di

import brillembourg.parser.emovie.data.network_imp.MovieApi
import brillembourg.parser.emovie.data.NetworkDataSource
import brillembourg.parser.emovie.data.network_imp.RetrofitNetworkDataSource
import brillembourg.parser.emovie.data.network_imp.SERVER_URL
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkSourceModule {

    @Singleton
    @Provides
    fun provideNetworkDataSource(movieApi: MovieApi): NetworkDataSource = RetrofitNetworkDataSource(movieApi)

    @Singleton
    @Provides
    fun provideMovieApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
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
    fun provideHttpClient(): OkHttpClient {
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