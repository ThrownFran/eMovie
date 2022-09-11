package brillembourg.parser.emovie.di

import android.content.Context
import brillembourg.parser.emovie.data.local.*
import brillembourg.parser.emovie.data.local.categories.CategoryDao
import brillembourg.parser.emovie.data.local.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local.movies.MovieDao
import brillembourg.parser.emovie.data.local.trailers.TrailerDao
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
    fun movieDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.movieDao()
    }

    @Singleton
    @Provides
    fun categoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }


    @Singleton
    @Provides
    fun trailerDao(appDatabase: AppDatabase): TrailerDao {
        return appDatabase.trailerDao()
    }

    @Singleton
    @Provides
    fun movieCategoryCrossDao(appDatabase: AppDatabase): CategoryMoviesCrossDao {
        return appDatabase.movieCategoryCrossDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(
        appDatabase: AppDatabase,
        movieDao: MovieDao,
        categoryDao: CategoryDao,
        trailerDao: TrailerDao,
        crossDao: CategoryMoviesCrossDao
    ): MovieLocalDataSource =
        RoomDataSource(appDatabase, movieDao, trailerDao, crossDao)

    @Singleton
    @Provides
    fun getAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }

}