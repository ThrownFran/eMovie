package brillembourg.parser.emovie.presentation.detail

import androidx.lifecycle.SavedStateHandle
import brillembourg.parser.emovie.domain.use_cases.GetMovieDetailUseCase
import brillembourg.parser.emovie.presentation.models.toDomain
import brillembourg.parser.emovie.presentation.models.toPresentation
import brillembourg.parser.emovie.utils.CoroutineTestRule
import brillembourg.parser.emovie.utils.TestSchedulers
import brillembourg.parser.emovie.utils.movieDomainFakes
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
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

    private lateinit var SUT: DetailViewModel

    private val movieFake = movieDomainFakes[0].toPresentation()

    private val savedStateHandle = SavedStateHandle(HashMap<String, Any?>().apply {
        put("movie", movieFake)
    })

    @Before
    fun setUp() {
        coEvery { getMovieDetailUseCase.invoke(any()) }.coAnswers { flow { emit(movieFake.toDomain()) } }
        SUT = DetailViewModel(savedStateHandle, TestSchedulers(coroutineTestRule.testDispatcher), getMovieDetailUseCase)
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
}