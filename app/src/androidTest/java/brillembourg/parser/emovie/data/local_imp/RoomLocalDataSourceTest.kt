@file:OptIn(ExperimentalCoroutinesApi::class)

package brillembourg.parser.emovie.data.local_imp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import brillembourg.parser.emovie.data.MoviePage
import brillembourg.parser.emovie.data.local_imp.movies.MovieTable
import brillembourg.parser.emovie.data.local_imp.movies.toData
import brillembourg.parser.emovie.data.local_imp.trailers.toDomain
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Trailer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate

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
    fun getMovies_returnMoviesMappedToData_and_sortedByOrder() = runTest {
        //Arrange
        val category = Category.TopRated
        val movies = movies.map { it.toData() }.shuffled()
        SUT.prepopulateCategories()
        SUT.saveMovies(category, MoviePage(movies, 1, 1), 0)
        //Act
        val moviesFromDb = SUT.getMovies(category).first()
        //Assert
        assertEquals(movies, moviesFromDb)

        //Movies are in order
        moviesFromDb.mapIndexed { index, movie ->
            val remoteKey = remoteKeyDao.getRemoteKeyForMovie(movie.id, category.key)
            Pair(remoteKey?.order, movie)
        }.zipWithNext { a, b ->
            //Check order with next value
            assertTrue((a.first ?: 0) < (b.first ?: 0))
        }
    }

    @Test
    fun deleteMovies_thenDeleteCategoryCrossEntityAndMovieEntity() = runTest {
        //Arrange
        SUT.prepopulateCategories()
        SUT.saveMovies(
            category = Category.TopRated,
            moviePage = MoviePage(movies.map { it.toData() },
                1,
                4),
            nextOrder = 1)
        //Act
        SUT.deleteMovies(Category.TopRated)
        val movies = SUT.getMovies(Category.TopRated).first()
        //Assert
        assertEquals(0, movies.size)

        val crossList = crossDao.getList().filter { it.categoryKey == Category.TopRated.key }
        assertEquals(0, crossList.size)
    }

    @Test
    fun saveMovies_then_saveMoviesInDao_and_saveCategoryMoviesCrossList_and_saveRemoteKeys() =
        runTest {
            //Arrange
            val page = 1
            val lastPage = 3
            val order = 0
            val category = Category.TopRated
            val moviesMocked = movies
            val pageToSave = MoviePage(
                movies = moviesMocked.map { it.toData() },
                currentPage = page,
                lastPage = lastPage
            )
            SUT.prepopulateCategories()

            //Act
            SUT.saveMovies(Category.TopRated, pageToSave, order)

            //ASSERT

            //Movies are saved correctly
            val movies = SUT.getMovies(Category.TopRated).first()
            assertEquals(moviesMocked.map { it.toData() }, movies)

            //CategoryMoviesCross are saved correctly
            val categoryMovieCrossList = crossDao.getList()
            categoryMovieCrossList.forEach { crossTable ->
                //Movie id is saved
                assertNotNull(moviesMocked.find { it.id == crossTable.movieId })
                //Category key is saved
                assertEquals(crossTable.categoryKey, category.key)
            }

            //Save remote keys
            val remoteKeys = remoteKeyDao.getRemoteKeysForCategory(category.key)
            remoteKeys.forEach { remoteKey ->
                //Movie id is saved
                assertNotNull(moviesMocked.find { remoteKey.movieId == it.id })
                //Page data is saved
                assertEquals(remoteKey.currentPage, page)
                assertEquals(remoteKey.lastPage, lastPage)
                //Category key is saved
                assertEquals(remoteKey.categoryKey, category.key)
            }

            //Check remote key order
            moviesMocked.forEachIndexed { index, movieTable ->
                val remoteKeyToCheck =
                    remoteKeyDao.getRemoteKeyForMovie(movieTable.id, category.key)
                assertEquals(order + index, remoteKeyToCheck?.order)
            }
        }

    @Test
    fun saveTrailers_ThenCallTrailerDaoWithCorrectParams () = runTest {
        //Arrange
        val category = Category.Upcoming
        val movieId = 100L
        val movies = (movies + listOf(movies[0].copy(id = movieId))).map { it.toData() }
        val trailers = (1..5).map { Trailer(it.toString(),"key","name","",movieId) }
        //Act
        SUT.prepopulateCategories()
        SUT.saveMovies(category, MoviePage(movies,1,1),0)
        SUT.saveTrailers(trailers)
        //Assert
        val result = trailerDao.getMovieWithTrailers(movieId).first().trailers
        assertEquals(trailers,result.map { it.toDomain() })
    }

    @Test
    fun getTrailers_thenReturnTrailersMappedAsDomain () = runTest {
        //Arrange
        val movieId = 120L
        val category = Category.TopRated
        val movies = (movies + listOf(movies[0].copy(id = movieId))).map { it.toData() }
        val trailers = (1..5).map { Trailer(it.toString(),"key","name","",movieId) }
        SUT.prepopulateCategories()
        SUT.saveMovies(category,MoviePage(movies,1,1),0)
        SUT.saveTrailers(trailers)
        //Act
        val result = SUT.getTrailers(movieId).first()
        //Assert
        assertEquals(trailers,result)
    }




}