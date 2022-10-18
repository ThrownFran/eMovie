package brillembourg.parser.emovie.data.local_imp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import brillembourg.parser.emovie.data.NetworkDataSource
import brillembourg.parser.emovie.data.local_imp.categories.CategoryDao
import brillembourg.parser.emovie.data.local_imp.categories.CategoryTable
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMovieCrossRef
import brillembourg.parser.emovie.data.local_imp.category_movie_cross.CategoryMoviesCrossDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieDao
import brillembourg.parser.emovie.data.local_imp.movies.MovieTable
import brillembourg.parser.emovie.data.local_imp.movies.toData
import brillembourg.parser.emovie.data.local_imp.remote_keys.RemoteKeyDao
import brillembourg.parser.emovie.domain.models.Category
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import java.util.concurrent.CountDownLatch


@RunWith(AndroidJUnit4::class)
class RoomLocalDataSourceTest {

    @get:Rule
    val coroutineTestRule = CoroutineAndroidTestRule()

    private var dataBase: AppDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        AppDatabase::class.java
    ).allowMainThreadQueries().build()

    private val categoryDao = dataBase.categoryDao()
    private val movieDao = dataBase.movieDao()
    private val crossDao = dataBase.movieCategoryCrossDao()
    private val remoteKeyDao = dataBase.remoteKeyDao()
    private val trailerDao = dataBase.trailerDao()

    private lateinit var SUT: RoomLocalDataSource

    private val movies = (1..10).map {
        MovieTable(it.toLong(), "Movie $it", originalLanguage = "en",
            releaseDate = LocalDate.of(1993, 1, 1),
            backdropImageUrl = null,
            plot = "Plot",
            voteAverage = it.toFloat(),
            voteCount = 10
        )
    }

    @Before
    fun setUp() {
        SUT = RoomLocalDataSource(
            appDatabase = dataBase,
            movieDao = movieDao,
            trailerDao = trailerDao,
            crossDao = crossDao,
            categoryDao = categoryDao,
            remoteKeyDao = remoteKeyDao
        )
    }

    @After
    fun closeDatabase() {
        dataBase.close()
    }

    @Test
    fun prepopulateCategories_then_saveAllCategoriesKeys() = runTest {
        //Arrange
        //Act
        SUT.prepopulateCategories()
        //Assert
        val categories = categoryDao.getCategories()
        assertEquals(2, categories.size)
        assertEquals(Category.Upcoming.key,
            categories.first { categoryTable -> categoryTable.name == Category.Upcoming.key }.name)
        assertEquals(Category.TopRated.key,
            categories.first { categoryTable -> categoryTable.name == Category.TopRated.key }.name)
    }

    @Test
    fun getMovies_returnMoviesMappedToData() = runTest {
        //Arrange
        //Act
        SUT.prepopulateCategories()
        movies.forEach {
            movieDao.saveMovie(it)
            crossDao.create(CategoryMovieCrossRef(Category.TopRated.key, it.id))
        }
        val moviesFromDb = SUT.getMovies(Category.TopRated).first()
        //Assert
        assertEquals(movies.map { it.toData() }, moviesFromDb)
    }

    @Test
    fun deleteMovies_thenDeleteCategoryCrossEntityAndMovieEntity() = runTest {
        //Arrange
        SUT.prepopulateCategories()
        SUT.saveMovies(
            category = Category.TopRated,
            moviePageResponse = NetworkDataSource.MoviePageResponse(movies.map { it.toData() },
                1,
                4),
            nextOrder = 1)
        //Act
        SUT.deleteMovies(Category.TopRated)
        val movies = SUT.getMovies(Category.TopRated).first()
        //Assert
        assertEquals(0,movies.size)

        val crossList = crossDao.getList().filter { it.categoryKey == Category.TopRated.key }
        assertEquals(0,crossList.size)
    }
}