package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.utils.CoroutineTestRule
import brillembourg.parser.emovie.utils.TestSchedulers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class RefreshMovieDetailUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var repository: MovieRepository

    private lateinit var SUT: RefreshMovieDetailUseCase

    @Before
    fun setUp() {
        coEvery { repository.refreshMovieDetail(any()) } returns(Unit)
        SUT = RefreshMovieDetailUseCase(TestSchedulers(),repository)
    }

    @Test
    fun `given invoke, then refresh with correct params`() = runTest {
        //Arrange
        val movieId = 10L
        //Act
        SUT.invoke(movieId)
        //Assert
        coVerify { repository.refreshMovieDetail(match { params -> params == movieId }) }
    }

}