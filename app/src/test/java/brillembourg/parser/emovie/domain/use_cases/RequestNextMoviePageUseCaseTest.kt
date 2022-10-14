package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.utils.CoroutineTestRule
import brillembourg.parser.emovie.utils.TestSchedulers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class RequestNextMoviePageUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var repository: MovieRepository

    private lateinit var SUT: RequestNextMoviePageUseCase

    @Before
    fun setUp() {
        coEvery { repository.requestNextMoviePage(any(), any()) }.returns(RequestNextMoviePageUseCase.Result.RequestSuccess)
        SUT = RequestNextMoviePageUseCase(TestSchedulers(), repository)
    }

    @Test
    fun `given invoke, then request next page with correct params`() = runTest {
        //Arrange
        val category = Category.TopRated
        val page = 3
        //Act
        SUT.invoke(category, page)
        //Assert
        coVerify {
            repository.requestNextMoviePage(
                match { params -> params == category },
                match { params -> params == page })
        }
    }




}