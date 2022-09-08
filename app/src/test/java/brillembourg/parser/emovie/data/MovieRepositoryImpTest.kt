package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.CoroutineTestRule
import brillembourg.parser.emovie.TestSchedulers
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.GetMovieList
import brillembourg.parser.emovie.domain.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
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
    lateinit var localDataSource: MovieLocalDataSource

    @MockK
    lateinit var networkDataSource: MovieNetworkDataSource

    lateinit var SUT: MovieRepository

    private val movieFakes = listOf(
        MovieData(1L, "Movie 1", ""),
        MovieData(2L, "Movie 1", ""),
        MovieData(3L, "Movie 1", ""),
        MovieData(41L, "Movie 1", "")
    )

    @Before
    fun setUp() {
        SUT = MovieRepositoryImp(TestSchedulers(), localDataSource, networkDataSource)
    }

    @Test
    fun `given get Movies, any category, then parameter is passed correctly to local data source`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockSuccessDatasource()
        //Act
        SUT.getMovies(category)
        //Assert
        coVerify { localDataSource.getMovies(match { params -> params == category }) }
    }

    @Test
    fun `given get Movies, any category, then save in local data source`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockSuccessDatasource()
        //Act
        val result = SUT.getMovies(category).first()
        //Assert
        coVerify {
            localDataSource.saveMovies(
                match { params ->
                    params == result.map { it.toData() }
                }
            )
        }
    }

    @Test
    fun `given get Movies, is success, then fetch network data source`() = runTest {
        //Arrange
        val category = Category.TopRated()
        mockSuccessDatasource()
        //Act
        SUT.getMovies(category).collect()
        //Assert
        coVerify {
            networkDataSource.getMovies(
                match { params ->
                    params == category
                }
            )
        }
    }

    @Test
    fun `given get Movies, is error, then propagate exception`() = runTest {
//        //Arrange
//        val category = Category.TopRated()
//        mockSuccessDatasource()
//        //Act
//        SUT.getMovies(category)
//        //Assert
//        coVerify {
//            networkDataSource.getMovies(
//                match { params ->
//                    params == category
//                }
//            )
//        }
    }


    private fun mockSuccessDatasource() {
        coEvery { localDataSource.getMovies(any()) }.coAnswers { flow { emit(movieFakes) } }
        coEvery { localDataSource.saveMovies(any()) }.returns(Unit)
        coEvery { networkDataSource.getMovies(any()) }.returns(movieFakes)
    }

//    @Test
//    fun `given get Movies, then parameter is passed correctly`() = runTest {
//        //Arrange
//        val category = Category.TopRated()
//        coEvery { networkDataSource.getMovies(any()) }.coAnswers { movieFakes }
//        //Act
//        SUT.getMovies(category)
//        //Assert
//        coVerify { networkDataSource.getMovies(match { params -> params == category }) }
//    }


}