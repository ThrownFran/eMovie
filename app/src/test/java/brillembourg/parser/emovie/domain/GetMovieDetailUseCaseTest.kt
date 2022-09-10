package brillembourg.parser.emovie.domain

import brillembourg.parser.emovie.domain.use_cases.GetMovieDetailUseCase
import brillembourg.parser.emovie.domain.use_cases.GetMoviesUseCase
import brillembourg.parser.emovie.utils.CoroutineTestRule
import brillembourg.parser.emovie.utils.TestSchedulers
import brillembourg.parser.emovie.utils.movieDomainFakes
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
class GetMovieDetailUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var repository: MovieRepository

    lateinit var SUT: GetMovieDetailUseCase

    @Before
    fun setUp() {
        SUT = GetMovieDetailUseCase(TestSchedulers(),repository)
    }

    @Test
    fun `given invoke, then correct parameters are passed`() = runTest {
        //Arrange
        val id = 4L
        mockGetMoviesFromRepository()
        //Act
        SUT.invoke(id)
        //Assert
        coVerify { SUT.invoke(match { params -> params == id }) }
    }

    private fun mockGetMoviesFromRepository() {
        coEvery { repository.getMovie(any()) }.coAnswers { flow { emit(movieDomainFakes[0]) } }
    }

}