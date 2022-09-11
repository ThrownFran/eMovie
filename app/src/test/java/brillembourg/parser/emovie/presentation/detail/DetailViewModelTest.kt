package brillembourg.parser.emovie.presentation.detail

import androidx.lifecycle.SavedStateHandle
import brillembourg.parser.emovie.data.NetworkException
import brillembourg.parser.emovie.domain.use_cases.GetMovieDetailUseCase
import brillembourg.parser.emovie.domain.use_cases.RefreshMovieDetailUseCase
import brillembourg.parser.emovie.presentation.models.toDomain
import brillembourg.parser.emovie.presentation.models.toPresentation
import brillembourg.parser.emovie.presentation.utils.UiText
import brillembourg.parser.emovie.utils.CoroutineTestRule
import brillembourg.parser.emovie.utils.TestSchedulers
import brillembourg.parser.emovie.utils.movieDetailFake
import brillembourg.parser.emovie.utils.movieDomainFakes
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DetailViewModelTest {

    @get:Rule
    val rule = MockKRule(this)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule(StandardTestDispatcher())

    @MockK
    lateinit var getMovieDetailUseCase: GetMovieDetailUseCase

    @MockK
    lateinit var refreshMovieDetailUseCase: RefreshMovieDetailUseCase

    private lateinit var SUT: DetailViewModel

    private val movieFake = movieDomainFakes[0].toPresentation()

    private val savedStateHandle = SavedStateHandle(HashMap<String, Any?>().apply {
        put("movie", movieFake)
    })

    @Before
    fun setUp() {
        coEvery { getMovieDetailUseCase.invoke(any()) }.coAnswers { flow { emit(movieDetailFake) } }
        coEvery { refreshMovieDetailUseCase.invoke(any()) }.returns(Unit)
        SUT = DetailViewModel(
            savedStateHandle = savedStateHandle,
            schedulers = TestSchedulers(coroutineTestRule.testDispatcher),
            getMovieDetailUseCase = getMovieDetailUseCase,
            refreshMovieDetailUseCase = refreshMovieDetailUseCase
        )
    }

    @Test
    fun `given init, then ui state gets movie from savedStateHandle`() = runTest {
        //Arrange
        //Act
        //Assert
        assertEquals(SUT.detailUiState.value.movie, movieFake)
    }

    @Test
    fun `given init, observe movie flow`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        coVerify { getMovieDetailUseCase.invoke(movieFake.id) }
    }

    @Test
    fun `give init, when flow is received, update detail state`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        Assert.assertEquals(movieDetailFake.movie.toPresentation(), SUT.detailUiState.value.movie)
    }

    @Test
    fun `give init, then request refresh movie details use case`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        coVerify { refreshMovieDetailUseCase.invoke(movieFake.id) }
    }

    @Test
    fun `give request refresh movie, then show loading state during call and hide after call`() = runTest {
        //Arrange
        coEvery { refreshMovieDetailUseCase.invoke(any()) }.coAnswers {
            delay(300)
            movieFake
        }
        //Act
        advanceTimeBy(100)
        //Assert
        assertTrue(SUT.detailUiState.value.isLoading)
        //Act
        advanceTimeBy(400)
        //Assert
        assertFalse(SUT.detailUiState.value.isLoading)
    }

    @Test
    fun `give request refresh movie, when success, hide any loading state`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        //Assert
        assertFalse(SUT.detailUiState.value.isLoading)
    }

    @Test
    fun `give request refresh movie, when error, show error message to user and hide any loading state`() = runTest {
        //Arrange
        coEvery { refreshMovieDetailUseCase.invoke(any()) }.throws(NetworkException())
        //Act
        advanceUntilIdle()
        //Assert
        assertEquals(SUT.detailUiState.value.messageToShow, UiText.NoInternet)
        assertFalse(SUT.detailUiState.value.isLoading)
    }

    @Test
    fun `give on message shown, then update ui message state to null`() = runTest {
        //Arrange
        coEvery { refreshMovieDetailUseCase.invoke(any()) }.throws(NetworkException())
        //Act
        advanceUntilIdle()
        SUT.onMessageShown()
        //Assert
        assertNull(SUT.detailUiState.value.messageToShow)
    }

    @Test
    fun `give on Refresh, then execute refresh movie detail use case`() = runTest {
        //Arrange
        //Act
        advanceUntilIdle()
        SUT.onRefresh()
        advanceUntilIdle()
        //Assert
        //First one is initial load, Second is when onRefresh action
        coVerify(exactly = 2) { refreshMovieDetailUseCase.invoke(movieDetailFake.movie.id) }
        assertNull(SUT.detailUiState.value.messageToShow)
    }
}