package brillembourg.parser.emovie.di

import android.content.Context
import brillembourg.parser.emovie.data.local.AppDatabase
import brillembourg.parser.emovie.data.local.MovieDao
import brillembourg.parser.emovie.data.local.MovieLocalDataSource
import brillembourg.parser.emovie.data.local.RoomDataSource
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
    fun movieDao (appDatabase: AppDatabase): MovieDao {
        return appDatabase.movieDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(movieDao: MovieDao): MovieLocalDataSource = RoomDataSource(movieDao)

    @Singleton
    @Provides
    fun getAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }

}