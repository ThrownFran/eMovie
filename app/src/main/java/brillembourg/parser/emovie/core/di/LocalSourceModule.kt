package brillembourg.parser.emovie.core.di

import android.content.Context
import brillembourg.parser.emovie.data.LocalDataSource
import brillembourg.parser.emovie.data.local_imp.*
import brillembourg.parser.emovie.data.local_imp.categories.CategoryDao
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieDao
import brillembourg.parser.emovie.data.local_imp.remote_keys.RemoteKeyDao
import brillembourg.parser.emovie.data.local_imp.trailers.TrailerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalSourceModule {


    @Singleton
    @Provides
    fun provideMovieDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.movieDao()
    }

    @Singleton
    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Singleton
    @Provides
    fun provideRemoteKeyDao(appDatabase: AppDatabase): RemoteKeyDao {
        return appDatabase.remoteKeyDao()
    }


    @Singleton
    @Provides
    fun provideTrailerDao(appDatabase: AppDatabase): TrailerDao {
        return appDatabase.trailerDao()
    }

    @Singleton
    @Provides
    fun provideCategoryMoviesCrossDao(appDatabase: AppDatabase): CategoryMoviesCrossDao {
        return appDatabase.movieCategoryCrossDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(
        appDatabase: AppDatabase,
        movieDao: MovieDao,
        trailerDao: TrailerDao,
        crossDao: CategoryMoviesCrossDao,
        remoteKeyDao: RemoteKeyDao
    ): LocalDataSource =
        RoomLocalDataSource(appDatabase, movieDao, trailerDao, crossDao,remoteKeyDao)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }

}