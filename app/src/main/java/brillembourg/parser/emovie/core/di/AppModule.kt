package brillembourg.parser.emovie.core.di

import brillembourg.parser.emovie.data.MovieRepositoryImp
import brillembourg.parser.emovie.data.LocalDataSource
import brillembourg.parser.emovie.data.NetworkDataSource
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.core.Schedulers
import brillembourg.parser.emovie.core.SchedulersImp
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
        localDataSource: LocalDataSource,
        networkDataSource: NetworkDataSource
    ): MovieRepository = MovieRepositoryImp(schedulers,localDataSource,networkDataSource)

    @Singleton
    @Provides
    fun provideDispatchers(): Schedulers {
        return SchedulersImp()
    }


}


