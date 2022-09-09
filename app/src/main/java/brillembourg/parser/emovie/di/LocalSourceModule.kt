package brillembourg.parser.emovie.di

import android.content.Context
import brillembourg.parser.emovie.data.local.*
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
    fun movieCategoryCrossDao(appDatabase: AppDatabase): CategoryMoviesCrossDao {
        return appDatabase.movieCategoryCrossDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(
        movieDao: MovieDao,
        categoryDao: CategoryDao,
        crossDao: CategoryMoviesCrossDao
    ): MovieLocalDataSource =
        RoomDataSource(movieDao, categoryDao, crossDao)

    @Singleton
    @Provides
    fun getAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }

}