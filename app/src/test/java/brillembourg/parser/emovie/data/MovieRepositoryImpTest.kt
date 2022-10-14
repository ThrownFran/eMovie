package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.core.GenericException
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.domain.use_cases.RefreshMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RequestNextMoviePageUseCase
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
import kotlinx.coroutines.test.advanceUntilIdle
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
        mockLocalDataSourceSuccess()
        SUT = MovieRepositoryImp(TestSchedulers(coroutineTestRule.testDispatcher),
            localDataSource,
            networkDataSource)
    }

    @Test
    fun `given init, then prepopulate categories in local data source`() = runTest {
        //Arrange
        mockSuccessDatasource()
        //Act
        advanceUntilIdle()
        //Assert
        coVerify(exactly = 1) { localDataSource.prepopulateCategories() }
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
            val category = Category.TopRated
            mockSuccessDatasource()
            //Act
            SUT.getMovies(category)
            //Assert
            coVerify { localDataSource.getMovies(match { params -> params == category }) }
        }

    @Test
    fun `given get Movies, when success, then result is mapped to domain`() = runTest {
        //Arrange
        val category = Category.TopRated
        mockSuccessDatasource()
        //Act
        val result = SUT.getMovies(category).first()
        //Assert
        Assert.assertTrue(result == movieDataFakes.map { it.toDomain() })
    }

    @Test
    fun `given get Movies, when error, then propagate error as domain`() = runTest {
        //Arrange
        val category = Category.TopRated
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
            val category = Category.TopRated
            mockSuccessDatasource()
            //Act
            SUT.refreshMovies(category)
            //Assert
            coVerify { networkDataSource.getMovies(match { params -> params == category }) }
            coVerify { localDataSource.getMovies(match { params -> params == category }) }
        }

    @Test
    fun `given refresh movies data, when network source is success, then save network movies in local data source`() =
        runTest {
            //Arrange
            val category = Category.TopRated
            val networkMovies =
                movieDataFakes + movieDomainFakes[0].copy(name = "Movie 20", id = 20L).toData()
            val localMovies = movieDataFakes
            mockNetworkDataSourceSuccess(networkMovies)
            mockLocalDataSourceSuccess(localMovies)

            //Act
            SUT.refreshMovies(category)

            //Assert
            coVerify {
                localDataSource.saveMovies(category,
                    match { params -> params.movies == networkMovies },
                    localMovies.size)
            }
        }

    @Test
    fun `given refresh movies data, when invalidateCache flag is true, then delete movies from local source`() =
        runTest {
            //Arrange
            val category = Category.TopRated
            mockLocalDataSourceSuccess(movieDataFakes)
            mockNetworkDataSourceSuccess(movieDataFakes)
            //Act
            SUT.refreshMovies(category = category, invalidateCache = true)
            //Assert
            coVerify(exactly = 1) { localDataSource.deleteMovies(category) }
        }

    @Test
    fun `given refresh movies data, when invalidateCache flag is false, then dont delete movies from local source`() =
        runTest {
            //Arrange
            val category = Category.TopRated
            mockLocalDataSourceSuccess(movieDataFakes)
            mockNetworkDataSourceSuccess(movieDataFakes)
            //Act
            SUT.refreshMovies(category = category, invalidateCache = false)
            //Assert
            coVerify(exactly = 0) { localDataSource.deleteMovies(category) }
        }

    @Test
    fun `given refresh movies data, when network source has error, rethrow error as domain`() =
        runTest {
            //Arrange
            val category = Category.TopRated
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

    @Test
    fun `given refresh movies data, when is first and last page, then return correct result`() = runTest {
        //Arrange
        val category = Category.TopRated
        mockNetworkDataSourceSuccess(currentMoviePage = 1, lastMoviePage = 1)
        mockLocalDataSourceSuccess()
        //Act
        val result = SUT.refreshMovies(category)
        //Assert
        Assert.assertEquals(RefreshMoviesUseCase.Result.IsFirstAndLastPage, result)
    }

    @Test
    fun `given refresh movies data, when is last page is more than first page, then return has more pages to load result`() = runTest {
        //Arrange
        val category = Category.TopRated
        mockNetworkDataSourceSuccess(currentMoviePage = 1, lastMoviePage = 100)
        mockLocalDataSourceSuccess()
        //Act
        val result = SUT.refreshMovies(category)
        //Assert
        Assert.assertEquals(RefreshMoviesUseCase.Result.HasMorePagesToLoad, result)
    }

    @Test
    fun `given request next movie page, when last item not reached yet, then do nothing and return result LastPageInItemNotReachedYet`() = runTest {
        //Arrange
        val category = Category.TopRated
        mockLocalDataSourceSuccess(movieDataFakes20)
        mockNetworkDataSourceSuccess()
        //Act
        val result = SUT.requestNextMoviePage(category,5)
        //Assert
        Assert.assertEquals(result, RequestNextMoviePageUseCase.Result.LastItemInPageNotReachedYet)
        coVerify(exactly = 0) { networkDataSource.getMovies(category, any()) }
        coVerify(exactly = 0) { localDataSource.saveMovies(category,any(),any()) }
    }

    @Test
    fun `given request next movie page, when first page is last page, then return last page result and dont request next page`() =
        runTest {
            //Arrange
            val category = Category.TopRated
            mockLocalDataSourceSuccess(movieDataFakes20, lastPage = 1)
            mockNetworkDataSourceSuccess()
            val lastVisibleItem = movieDataFakes20.size - 1
            //Act
            val result = SUT.requestNextMoviePage(category, lastVisibleItem)
            advanceUntilIdle()
            //Assert
            Assert.assertEquals(result, RequestNextMoviePageUseCase.Result.LastPageAlreadyReached)
            coVerify(exactly = 0) { networkDataSource.getMovies(category, any()) }
            coVerify(exactly = 0) { localDataSource.saveMovies(category,any(),any()) }
        }

    @Test
    fun `given request next movie page, when page is not last, save results to local data source and return success`() =
        runTest {
            //Arrange
            val category = Category.TopRated
            val movieFakesSecondPage = movieDataFakes20.map { it.copy(id = it.id + 100) }
            mockNetworkDataSourceSuccess(movieFakesSecondPage)
            mockLocalDataSourceSuccess(movieDataFakes20, lastPage = 2)
            val lastVisibleItem = movieDataFakes20.size - 1
            //Act
            val result = SUT.requestNextMoviePage(category, lastVisibleItem)
            //Assert
            coVerify { networkDataSource.getMovies(category, 2) }
            coVerify {
                localDataSource.saveMovies(any(),
                    match { moviePageResponse -> moviePageResponse.movies == movieFakesSecondPage },
                    movieDataFakes20.size)
            }
            Assert.assertEquals(result, RequestNextMoviePageUseCase.Result.RequestSuccess)
        }

    @Test
    fun `given request next movie page, when item visible is not last, do nothing`() =
        runTest {
            //Arrange
            val category = Category.TopRated
            val movieFakesSecondPage = movieDataFakes20.map { it.copy(id = it.id + 100) }
            mockNetworkDataSourceSuccess(movieFakesSecondPage)
            mockLocalDataSourceSuccess(movieDataFakes20)
            //Act
            SUT.requestNextMoviePage(category, PAGE_SIZE - 10)
            //Assert
            coVerify(exactly = 0) { networkDataSource.getMovies(category, any()) }
            coVerify(exactly = 0) { localDataSource.saveMovies(any(), any(), any()) }
        }


    private fun mockSuccessDatasource() {
        mockLocalDataSourceSuccess(movieDataFakes)
        mockNetworkDataSourceSuccess(movieDataFakes)
    }

    private fun mockNetworkDataSourceSuccess(
        movieFakes: List<MovieData> = movieDataFakes,
        currentMoviePage : Int = 1,
        lastMoviePage: Int = 50,
        trailers: List<Trailer> = trailerFakes,
    ) {
        coEvery { networkDataSource.getTrailers(any()) }.returns(trailers)
        coEvery { networkDataSource.getMovies(any(), any()) }.returns(
            NetworkDataSource.MoviePageResponse(
                movies = movieFakes,
                currentPage = currentMoviePage,
                lastPage = lastMoviePage
            )
        )
    }

    private fun mockNetworkDataSourceError() {
        coEvery { networkDataSource.getMovies(any()) }.throws(Exception("Error"))
    }

    private fun mockLocalDataSourceSuccess(
        movieFakes: List<MovieData> = movieDataFakes,
        trailers: List<Trailer> = trailerFakes,
        lastPage: Int = 1
    ) {
        mockTrailerSuccess(trailers)
        coEvery { localDataSource.getMovies(any()) }.coAnswers { flow { emit(movieFakes) } }
        coEvery { localDataSource.saveMovies(any(), any(), any()) }.returns(Unit)
        coEvery { localDataSource.getMovie(any()) }.coAnswers { flow { emit(movieFakes[0]) } }
        coEvery { localDataSource.saveTrailers(any()) }.returns(Unit)
        coEvery { localDataSource.deleteMovies(any()) }.returns(Unit)
        coEvery { localDataSource.getLastPageForCategory(any()) }.returns(lastPage)
        coEvery { localDataSource.prepopulateCategories() }.coAnswers { Unit }
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