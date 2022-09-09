package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.data.local.MovieLocalDataSource
import brillembourg.parser.emovie.data.network.MovieNetworkDataSource
import brillembourg.parser.emovie.utils.CoroutineTestRule
import brillembourg.parser.emovie.utils.TestSchedulers
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.utils.movieDataFakes
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
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MovieRepositoryImpTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var localDataSource: MovieLocalDataSource

    @MockK
    lateinit var networkDataSource: MovieNetworkDataSource

    lateinit var SUT: MovieRepository

    @Before
    fun setUp() {
        SUT = MovieRepositoryImp(TestSchedulers(), localDataSource, networkDataSource)
    }

    @Test
    fun `given get Movies, when any category, then parameter is passed correctly to local data source`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockSuccessDatasource()
        //Act
        SUT.getMovies(category)
        //Assert
        coVerify { localDataSource.getMovies(match { params -> params == category }) }
    }

    @Test
    fun `given get Movies, when any category, then result is mapped to domain`() = runTest {
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
        var errorToCapture : Exception ? = null
        //Act
        SUT.getMovies(category)
            .catch { errorToCapture = it as Exception }
            .collect()
        //Assert
        Assert.assertNotNull(errorToCapture)
        Assert.assertTrue(errorToCapture is GenericException)
    }

    @Test
    fun `given refresh data, then fetch network and local data source with correct params`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockSuccessDatasource()
        //Act
        SUT.refreshData(category)
        //Assert
        coVerify { networkDataSource.getMovies(match { params -> params == category }) }
        coVerify { localDataSource.getMovies(match { params -> params == category }) }
    }

    @Test
    fun `given refresh data, when network movies are different from local movies, then save network movies in local data source`() = runTest {
        //Arrange
        val category = Category.TopRated()
        val networkMovies = movieDataFakes + MovieData(1L, "Movie 6", "","", Date.from(Instant.now()))
        val localMovies = movieDataFakes
        mockNetworkDataSourceSuccess(networkMovies)
        mockLocalDataSourceSuccess(localMovies)

        //Act
        SUT.refreshData(category)

        //Assert
        coVerify {
            localDataSource.saveMovies(category,
                match { params ->
                    params == networkMovies
                }
            )
        }
    }

    @Test
    fun `given refresh data, when network data is same as local data, then avoid saving to local source`() = runTest{
        //Arrange
        val category = Category.TopRated()
        mockLocalDataSourceSuccess(movieDataFakes)
        mockNetworkDataSourceSuccess(movieDataFakes)
        //Act
        SUT.refreshData(category)
        //Assert
        coVerify(exactly = 0) { localDataSource.saveMovies(any(),any())}
    }

    @Test
    fun `given refresh data, when network source has error, rethrow error as domain`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockNetworkDataSourceError()
        mockLocalDataSourceSuccess(movieDataFakes)
        var exceptionToCatch: Exception? = null
        //Act
        try {
            SUT.refreshData(category)
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

    private fun mockNetworkDataSourceSuccess(movieFakes : List<MovieData>) {
        coEvery { networkDataSource.getMovies(any()) }.returns(movieFakes)
    }

    private fun mockNetworkDataSourceError() {
        coEvery { networkDataSource.getMovies(any()) }.throws(Exception("Error"))
    }

    private fun mockLocalDataSourceSuccess(movieFakes : List<MovieData>) {
        coEvery { localDataSource.getMovies(any()) }.coAnswers { flow { emit(movieFakes) } }
        coEvery { localDataSource.saveMovies(any(),any()) }.returns(Unit)
    }

    private fun mockLocalDataSourceError() {
        coEvery { localDataSource.getMovies(any()) }.coAnswers { flow { throw Exception("Error") } }
    }



}