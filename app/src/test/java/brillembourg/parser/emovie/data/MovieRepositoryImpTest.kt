package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.core.GenericException
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.utils.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MovieRepositoryImpTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var localDataSource: LocalDataSource

    @MockK
    lateinit var networkDataSource: NetworkDataSource

    private lateinit var SUT: MovieRepository

    @Before
    fun setUp() {
        SUT = MovieRepositoryImp(TestSchedulers(coroutineTestRule.testDispatcher), localDataSource, networkDataSource)
    }

    @Test
    fun `given get Movie detail, then parameter is passed correctly to local data source`() =
        runTest {
            //Arrange
            val id = 5L
            mockSuccessDatasource()
            //Act
            SUT.getMovie(id).first()
            //Assert
            coVerify { localDataSource.getMovie(match { params -> params == id }) }
            coVerify { localDataSource.getTrailers(match { params -> params == id }) }
        }

    @Test
    fun `given get Movie detail, when success ,then results are passed to flow`() = runTest {
        //Arrange
        val id = 5L
        val movie = movieDomainFakes[0].toData().copy(id = id)
        coEvery { localDataSource.getMovie(any()) }.coAnswers { flow { emit(movie) } }
        mockTrailerSuccess()
        //Act
        val result = SUT.getMovie(id).first()
        //Assert
        Assert.assertEquals(movie.toDomain(), result.movie)
        Assert.assertEquals(trailerFakes, result.trailers)
    }

    @Test
    fun `given get Movie detail, when error, then propagate error as domain`() = runTest {
        //Arrange
        mockTrailerSuccess()
        mockLocalDataSourceError()
        var errorToCapture: Exception? = null
        //Act
        SUT.getMovie(7L)
            .catch { errorToCapture = it as Exception }
            .collect()
        //Assert
        Assert.assertNotNull(errorToCapture)
        Assert.assertTrue(errorToCapture is GenericException)
    }


    @Test
    fun `given get Movies, when any category, then parameter is passed correctly to local data source`() =
        runTest {
            //Arrange
            val category = Category.TopRated()
            mockSuccessDatasource()
            //Act
            SUT.getMovies(category)
            //Assert
            coVerify { localDataSource.getMovies(match { params -> params == category }) }
        }

    @Test
    fun `given get Movies, when success, then result is mapped to domain`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockSuccessDatasource()
        //Act
        val result = SUT.getMovies(category).first()
        //Assert
        Assert.assertTrue(result == movieDataFakes.map { it.toDomain() })
    }

    @Test
    fun `given get Movies, when error, then propagate error as domain`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockLocalDataSourceError()
        var errorToCapture: Exception? = null
        //Act
        SUT.getMovies(category)
            .catch { errorToCapture = it as Exception }
            .collect()
        //Assert
        Assert.assertNotNull(errorToCapture)
        Assert.assertTrue(errorToCapture is GenericException)
    }

    @Test
    fun `given refresh movie detail data, then fetch trailers from network and local data source with correct params`() =
        runTest {
            //Arrange
            mockSuccessDatasource()
            //Act
            SUT.refreshMovieDetail(10L)
            //Assert
            coVerify { networkDataSource.getTrailers(match { params -> params == 10L }) }
            coVerify { localDataSource.getTrailers(match { params -> params == 10L }) }
        }

    @Test
    fun `given refresh movie detail, when network data is different from local data, then save network trailers in local data source`() =
        runTest {
            //Arrange
            val id = 10L
            val networkTrailer = trailerFakes + TrailerFake("20").toDomain()
            val localTrailers = trailerFakes
            mockNetworkDataSourceSuccess(trailers = networkTrailer)
            mockLocalDataSourceSuccess(trailers = localTrailers)
            //Act
            SUT.refreshMovieDetail(id)
            //Assert
            coVerify {
                localDataSource.saveTrailers(match { params -> params == networkTrailer })
            }
        }

    @Test
    fun `given refresh movie detail, when network data is same as local data, then avoid saving to local source`() =
        runTest {
            //Arrange
            val id = 10L
            val networkTrailer = trailerFakes
            val localTrailers = trailerFakes
            mockNetworkDataSourceSuccess(trailers = networkTrailer)
            mockLocalDataSourceSuccess(trailers = localTrailers)
            //Act
            SUT.refreshMovieDetail(id)
            //Assert
            coVerify(exactly = 0) {
                localDataSource.saveTrailers(match { params -> params == networkTrailer })
            }
        }

    @Test
    fun `given refresh movie detail, when network source has error, rethrow error as domain`() =
        runTest {
            //Arrange
            mockNetworkDataSourceError()
            mockLocalDataSourceSuccess()
            var exceptionToCatch: Exception? = null
            //Act
            try {
                SUT.refreshMovieDetail(10L)
            } catch (e: Exception) {
                exceptionToCatch = e
            }
            //Assert
            Assert.assertNotNull(exceptionToCatch)
            Assert.assertTrue(exceptionToCatch is GenericException)
        }

    @Test
    fun `given refresh movies data, then fetch network and local data source with correct params`() =
        runTest {
            //Arrange
            val category = Category.TopRated()
            mockSuccessDatasource()
            //Act
            SUT.refreshMovies(category)
            //Assert
            coVerify { networkDataSource.getMovies(match { params -> params == category }) }
            coVerify { localDataSource.getMovies(match { params -> params == category }) }
        }

    @Test
    fun `given refresh movies data, when network movies are different from local movies, then save network movies in local data source`() =
        runTest {
            //Arrange
            val category = Category.TopRated()
            val networkMovies =
                movieDataFakes + movieDomainFakes[0].copy(name = "Movie 20", id = 20L).toData()
            val localMovies = movieDataFakes
            mockNetworkDataSourceSuccess(networkMovies)
            mockLocalDataSourceSuccess(localMovies)

            //Act
            SUT.refreshMovies(category)

            //Assert
            coVerify {
                localDataSource.saveMovies(category, match { params -> params == networkMovies })
            }
        }

    @Test
    fun `given refresh movies data, when network data is same as local data, then avoid saving to local source`() =
        runTest {
            //Arrange
            val category = Category.TopRated()
            mockLocalDataSourceSuccess(movieDataFakes)
            mockNetworkDataSourceSuccess(movieDataFakes)
            //Act
            SUT.refreshMovies(category)
            //Assert
            coVerify(exactly = 0) { localDataSource.saveMovies(any(), any()) }
        }

    @Test
    fun `given refresh movies data, when network source has error, rethrow error as domain`() =
        runTest {
            //Arrange
            val category = Category.TopRated()
            mockNetworkDataSourceError()
            mockLocalDataSourceSuccess(movieDataFakes)
            var exceptionToCatch: Exception? = null
            //Act
            try {
                SUT.refreshMovies(category)
            } catch (e: Exception) {
                exceptionToCatch = e
            }
            //Assert
            Assert.assertNotNull(exceptionToCatch)
            Assert.assertTrue(exceptionToCatch is GenericException)
        }


    private fun mockSuccessDatasource() {
        mockLocalDataSourceSuccess(movieDataFakes)
        mockNetworkDataSourceSuccess(movieDataFakes)
    }

    private fun mockNetworkDataSourceSuccess(
        movieFakes: List<MovieData> = movieDataFakes,
        trailers: List<Trailer> = trailerFakes
    ) {
        coEvery { networkDataSource.getTrailers(any()) }.returns(trailers)
        coEvery { networkDataSource.getMovies(any()) }.returns(movieFakes)
    }

    private fun mockNetworkDataSourceError() {
        coEvery { networkDataSource.getMovies(any()) }.throws(Exception("Error"))
    }

    private fun mockLocalDataSourceSuccess(
        movieFakes: List<MovieData> = movieDataFakes,
        trailers: List<Trailer> = trailerFakes
    ) {
        mockTrailerSuccess(trailers)
        coEvery { localDataSource.getMovies(any()) }.coAnswers { flow { emit(movieFakes) } }
        coEvery { localDataSource.saveMovies(any(), any()) }.returns(Unit)
        coEvery { localDataSource.getMovie(any()) }.coAnswers { flow { emit(movieFakes[0]) } }
        coEvery { localDataSource.saveTrailers(any()) }.returns(Unit)
    }

    private fun mockLocalDataSourceError() {
        coEvery { localDataSource.getMovies(any()) }.coAnswers { flow { throw Exception("Error") } }
        coEvery { localDataSource.getMovie(any()) }.coAnswers { flow { throw Exception("Error") } }
    }

    private fun mockTrailerSuccess(trailers: List<Trailer> = trailerFakes) {
        coEvery { localDataSource.getTrailers(any()) }.coAnswers {
            flow { emit(trailers) }
        }
    }


}