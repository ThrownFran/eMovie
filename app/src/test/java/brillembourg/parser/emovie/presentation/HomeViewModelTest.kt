package brillembourg.parser.emovie.presentation

import androidx.lifecycle.SavedStateHandle
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.GetMoviesUseCase
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
        coEvery { getMoviesUseCase.invoke(any()) }.coAnswers { flow { emit(movieDomainFakes) } }
        SUT = HomeViewModel(savedStateHandle,TestSchedulers(coroutineTestRule.testDispatcher), getMoviesUseCase,refreshMoviesUseCase)
    }

    @Test
    fun `given init, then refresh top rated movies`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        coVerify { refreshMoviesUseCase.invoke(match { params -> params is Category.TopRated }) }
    }

    @Test
    fun `given init, then refresh upcoming movies`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        coVerify { refreshMoviesUseCase.invoke(match { params -> params is Category.Upcoming }) }
    }

    @Test
    fun `given init, then observe top rated movies`() = runTest {
        //Arrange
        //Act
        //Assert
        coVerify { getMoviesUseCase.invoke(match { params -> params is Category.TopRated }) }
    }

    @Test
    fun `given init, then observe upcoming movies`() = runTest {
        //Arrange
        //Act
        //Assert
        coVerify { getMoviesUseCase.invoke(match { params -> params is Category.Upcoming }) }
    }

    @Test
    fun `given top rated movies flow received, then update home state`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        Assert.assertEquals(movieDomainFakes.map { it.toPresentation() }, SUT.homeState.value.topRatedMovies)
    }

    @Test
    fun `given upcoming movies flow received, then update home state`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        Assert.assertEquals(movieDomainFakes.map { it.toPresentation() }, SUT.homeState.value.upcomingMovies)
    }
}