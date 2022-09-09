package brillembourg.parser.emovie.presentation

import androidx.lifecycle.SavedStateHandle
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.GetMoviesUseCase
import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.domain.RefreshMoviesUseCase
import brillembourg.parser.emovie.presentation.home.HomeViewModel
import brillembourg.parser.emovie.utils.CoroutineTestRule
import brillembourg.parser.emovie.utils.TestSchedulers
import brillembourg.parser.emovie.utils.movieDomainFakes
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.threeten.bp.LocalDate


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class HomeViewModelTest {

    @get:Rule
    val rule = MockKRule(this)
    @get:Rule
    val coroutineTestRule = CoroutineTestRule(StandardTestDispatcher())

    @MockK
    private lateinit var getMoviesUseCase: GetMoviesUseCase
    @MockK
    private lateinit var refreshMoviesUseCase: RefreshMoviesUseCase
    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var SUT : HomeViewModel

    @Before
    fun setUp() {
    }

    private fun buildSUT() {
        SUT = HomeViewModel(
            savedStateHandle,
            TestSchedulers(coroutineTestRule.testDispatcher),
            getMoviesUseCase,
            refreshMoviesUseCase
        )
    }

    private fun mockGetMoviesSuccess() {
        coEvery { getMoviesUseCase.invoke(any()) }.coAnswers { flow { emit(movieDomainFakes) } }
    }

    @Test
    fun `given init, then refresh top rated movies`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        //Assert
        coVerify { refreshMoviesUseCase.invoke(match { params -> params is Category.TopRated }) }
    }

    @Test
    fun `given init, then refresh upcoming movies`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        //Assert
        coVerify { refreshMoviesUseCase.invoke(match { params -> params is Category.Upcoming }) }
    }

    @Test
    fun `given init, then observe top rated movies`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        //Assert
        coVerify { getMoviesUseCase.invoke(match { params -> params is Category.TopRated }) }
    }

    @Test
    fun `given init, then observe upcoming movies`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        //Assert
        coVerify { getMoviesUseCase.invoke(match { params -> params is Category.Upcoming }) }
    }

    @Test
    fun `given top rated movies flow received, then update home state`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        //Assert
        Assert.assertEquals(movieDomainFakes.map { it.toPresentation() }, SUT.homeState.value.topRatedMovies)
    }

    @Test
    fun `given upcoming movies flow received, then update home state`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        //Assert
        Assert.assertEquals(movieDomainFakes.map { it.toPresentation() }, SUT.homeState.value.upcomingMovies)
    }

    @Test
    fun `given upcoming movies flow received, then update recommended movies state`() = runTest {
        //Arrange
        coEvery { getMoviesUseCase.invoke(any()) }.coAnswers { flow { emit(listOf(
            Movie(1L,"Movie1",null,"en",LocalDate.ofYearDay(1993,1)),
            Movie(2L,"Movie2",null,"es",LocalDate.ofYearDay(1993,1)),
            Movie(3L,"Movie3",null,"en",LocalDate.ofYearDay(1995,1)),
            Movie(4L,"Movie4",null,"es",LocalDate.ofYearDay(1996,1)),
            Movie(5L,"Movie5",null,"en",LocalDate.ofYearDay(1997,1)),
            Movie(6L,"Movie6",null,"en",LocalDate.ofYearDay(1993,1)),
        )) } }
        buildSUT()
        //Act
        advanceUntilIdle()
        //Assert
        Assert.assertEquals(1993,SUT.homeState.value.recommendedMovies.currentYear)
        Assert.assertEquals("en",SUT.homeState.value.recommendedMovies.currentLanguage)
        Assert.assertEquals(listOf("en","es").sorted(),SUT.homeState.value.recommendedMovies.selectableLanguages.sorted())
        Assert.assertEquals(listOf(1993,1995,1996,1997),SUT.homeState.value.recommendedMovies.selectableYears)
        Assert.assertEquals(listOf(
            Movie(1L,"Movie1",null,"en",LocalDate.ofYearDay(1993,1)).toPresentation(),
            Movie(6L,"Movie6",null,"en",LocalDate.ofYearDay(1993,1)).toPresentation(),
        ),SUT.homeState.value.recommendedMovies.movies)
    }




}