package brillembourg.parser.emovie.domain

import brillembourg.parser.emovie.CoroutineTestRule
import brillembourg.parser.emovie.TestSchedulers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class GetMovieListTest {

    @get:Rule
    val mockkRule = MockKRule(this)
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var repository: MovieRepository

    lateinit var SUT: GetMovieList

    val movieFakes = listOf(
        Movie(1L,"Movie 1",""),
        Movie(1L,"Movie 2",""),
        Movie(1L,"Movie 3",""),
        Movie(1L,"Movie 4",""),
        Movie(1L,"Movie 5",""),
    )

    @Before
    fun setUp() {
        SUT = GetMovieList(TestSchedulers(),repository)
    }

    @Test
    fun `given invoke, when category is upcoming, then correct parameters are passed`() = runTest {
        //Arrange
        mockGetMoviesFromRepository()
        val upcomingCategory = Category.Upcoming()
        //Act
        SUT.invoke(upcomingCategory)
        //Assert
        coVerify { SUT.invoke(match { category -> category == upcomingCategory }) }
    }

    @Test
    fun `given invoke, when category is top rated, then correct parameters are passed`() = runTest {
        //Arrange
        mockGetMoviesFromRepository()
        val topRatedCategory = Category.TopRated()
        //Act
        SUT.invoke(topRatedCategory)
        //Assert
        coVerify { repository.getMovies(match { category -> category == topRatedCategory }) }
    }


    private fun mockGetMoviesFromRepository() {
        coEvery { repository.getMovies(any()) }.coAnswers { flow { emit(movieFakes) } }
    }

}