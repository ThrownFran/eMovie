package brillembourg.parser.emovie.di

import brillembourg.parser.emovie.data.MovieRepositoryImp
import brillembourg.parser.emovie.data.local.MovieLocalDataSource
import brillembourg.parser.emovie.data.local.RoomDataSource
import brillembourg.parser.emovie.data.network.MovieApi
import brillembourg.parser.emovie.data.network.MovieNetworkDataSource
import brillembourg.parser.emovie.data.network.RetrofitNetworkDataSource
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import brillembourg.parser.emovie.domain.SchedulersImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Singleton
    @Provides
    fun provideMovieRepository(
        schedulers: Schedulers,
        localDataSource: MovieLocalDataSource,
        networkDataSource: MovieNetworkDataSource
    ): MovieRepository = MovieRepositoryImp(schedulers,localDataSource,networkDataSource)

    @Singleton
    @Provides
    fun dispatchers(): Schedulers {
        return SchedulersImp()
    }


}


