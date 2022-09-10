package brillembourg.parser.emovie.presentation

import androidx.lifecycle.SavedStateHandle
import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.use_cases.GetMoviesUseCase
import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.domain.use_cases.RefreshMoviesUseCase
import brillembourg.parser.emovie.presentation.home.HomeViewModel
import brillembourg.parser.emovie.presentation.models.toPresentation
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

    private val movieFakesForRecommendedCategory: List<Movie> = listOf(
        Movie(1L, "Movie1", null, "en", LocalDate.ofYearDay(1993, 1)),
        Movie(2L, "Movie2", null, "es", LocalDate.ofYearDay(1993, 1)),
        Movie(3L, "Movie3", null, "en", LocalDate.ofYearDay(1995, 1)),
        Movie(4L, "Movie4", null, "es", LocalDate.ofYearDay(1996, 1)),
        Movie(5L, "Movie5", null, "en", LocalDate.ofYearDay(1997, 1)),
        Movie(6L, "Movie6", null, "en", LocalDate.ofYearDay(1993, 1)),
        Movie(7L, "Movie7", null, "en", LocalDate.ofYearDay(1993, 1)),
        Movie(8L, "Movie8", null, "en", LocalDate.ofYearDay(1993, 1)),
    )

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

    private lateinit var SUT: HomeViewModel

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
        Assert.assertEquals(
            movieDomainFakes.map { it.toPresentation() },
            SUT.homeUiState.value.topRatedMovies
        )
    }

    @Test
    fun `given upcoming movies flow received, then update home state`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        //Assert
        Assert.assertEquals(
            movieDomainFakes.map { it.toPresentation() },
            SUT.homeUiState.value.upcomingMovies
        )
    }

    @Test
    fun `given upcoming movies flow received, then recommended movies have current language and year filter null`() =
        runTest {
            //Arrange
            mockGetMoviesForRecommendedTests()
            buildSUT()
            //Act
            advanceUntilIdle()
            //Assert
            Assert.assertNull(SUT.homeUiState.value.recommendedMovies.languageFilter.currentLanguage)
            Assert.assertNull(SUT.homeUiState.value.recommendedMovies.yearFilter.currentYear)
        }

    @Test
    fun `given upcoming movies flow received, when movies contains languages en and es, then selectable languages is a list of en,es sorted`() =
        runTest {
            //Arrange
            mockGetMoviesForRecommendedTests()
            buildSUT()
            //Act
            advanceUntilIdle()
            //Assert
            Assert.assertEquals(
                listOf("en", "es").sorted(),
                SUT.homeUiState.value.recommendedMovies.languageFilter.selectableLanguages.sorted()
            )
        }

    @Test
    fun `given upcoming movies flow received, when movies contains different release years, then selectable years in year filter is a list of unique years sorted by desc`() =
        runTest {
            //Arrange
            mockGetMoviesForRecommendedTests()
            buildSUT()
            //Act
            advanceUntilIdle()
            //Assert
            Assert.assertEquals(
                listOf(1993, 1995, 1996, 1997).sortedDescending(),
                SUT.homeUiState.value.recommendedMovies.yearFilter.selectableYears
            )
        }

    @Test
    fun `given upcoming movies flow received, when filter has null values, then don't filter`() =
        runTest {
            //Arrange
            val list = movieFakesForRecommendedCategory.take(3)
            mockGetMoviesForRecommendedTests(list)
            buildSUT()
            //Act
            SUT.onSetNoYearFilter()
            SUT.onSetNoLanguageFilter()
            advanceUntilIdle()
            //Assert
            Assert.assertEquals(
                list.map { it.toPresentation() }, SUT.homeUiState.value.recommendedMovies.movies
            )
        }

    @Test
    fun `given upcoming movies flow received, when top rated movies count is more than 6, then take first 6`() =
        runTest {
            mockGetMoviesForRecommendedTests(
                movieFakesForRecommendedCategory + listOf(
                    Movie(
                        9L, "Movie 9", "", "en",
                        LocalDate.ofYearDay(1992, 1)
                    ),

                    Movie(
                        9L, "Movie 10", "", "es",
                        LocalDate.ofYearDay(1960, 1)
                    ),

                    Movie(
                        9L, "Movie 11", "", "en",
                        LocalDate.ofYearDay(2022, 1)
                    ),
                )
            )
            buildSUT()
            //Act
            advanceUntilIdle()
            //Assert
            Assert.assertEquals(
                6, SUT.homeUiState.value.recommendedMovies.movies.size
            )
        }


    @Test
    fun `given select year in recommended movies, then filter movie by year`() = runTest {
        //Arrange
        mockGetMoviesForRecommendedTests()
        buildSUT()
        //Act
        advanceUntilIdle()
        SUT.onYearFilterSelected(1997)
        //Arrange
        Assert.assertEquals(1997, SUT.homeUiState.value.recommendedMovies.yearFilter.currentYear)
        Assert.assertEquals(
            listOf(Movie(5L, "Movie5", null, "en", LocalDate.ofYearDay(1997, 1)).toPresentation()),
            SUT.homeUiState.value.recommendedMovies.movies
        )
    }

    @Test
    fun `given select language in recommended movies, then filter movie by language`() = runTest {
        //Arrange
        val movieInSpanish = Movie(2L, "Movie2", null, "es", LocalDate.ofYearDay(1993, 1))
        mockGetMoviesForRecommendedTests(
            listOf(
                Movie(1L, "Movie1", null, "en", LocalDate.ofYearDay(1993, 1)),
                movieInSpanish,
                Movie(3L, "Movie3", null, "en", LocalDate.ofYearDay(1995, 1)),
            )
        )
        buildSUT()
        //Act
        advanceUntilIdle()
        SUT.onLanguageFilterSelected("es")
        //Arrange
        Assert.assertEquals(
            "es",
            SUT.homeUiState.value.recommendedMovies.languageFilter.currentLanguage
        )
        Assert.assertEquals(
            listOf(movieInSpanish.toPresentation()),
            SUT.homeUiState.value.recommendedMovies.movies
        )
    }

    @Test
    fun `given select language and select year in recommended movies ,then filter movies by language and year`() =
        runTest {
            //Arrange
            mockGetMoviesForRecommendedTests(
                listOf(
                    Movie(1L, "Movie1", null, "en", LocalDate.ofYearDay(1993, 1)),
                    Movie(2L, "Movie2", null, "es", LocalDate.ofYearDay(1993, 1)),
                    Movie(3L, "Movie3", null, "ja", LocalDate.ofYearDay(1995, 1)),
                    Movie(4L, "Movie4", null, "en", LocalDate.ofYearDay(1993, 1)),
                    Movie(5L, "Movie5", null, "ja", LocalDate.ofYearDay(1993, 1)),
                    Movie(6L, "Movie6", null, "ja", LocalDate.ofYearDay(1995, 1)),
                )
            )
            buildSUT()

            //Act
            advanceUntilIdle()
            SUT.onLanguageFilterSelected("ja")
            SUT.onYearFilterSelected(1995)

            //Arrange

            //Check filter state
            Assert.assertEquals(
                "ja",
                SUT.homeUiState.value.recommendedMovies.languageFilter.currentLanguage
            )
            Assert.assertEquals(1995, SUT.homeUiState.value.recommendedMovies.yearFilter.currentYear)

            //Movies filtered
            Assert.assertEquals(
                listOf(
                    Movie(3L, "Movie3", null, "ja", LocalDate.ofYearDay(1995, 1)).toPresentation(),
                    Movie(6L, "Movie6", null, "ja", LocalDate.ofYearDay(1995, 1)).toPresentation()
                ),
                SUT.homeUiState.value.recommendedMovies.movies
            )
        }

    @Test
    fun `given click in movie, then navigate to movie`() {
        //Arrange
        val movieClicked = movieDomainFakes[0].toPresentation()
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        SUT.onMovieClick(movieClicked)
        //Assert
        Assert.assertEquals(SUT.homeUiState.value.navigateToThisMovie,movieClicked)
    }

    @Test
    fun `given navigation completed, then update navigate state to null`() {
        //Arrange
        val movieClicked = movieDomainFakes[0].toPresentation()
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        SUT.onMovieClick(movieClicked)
        SUT.onNavigateToMovieCompleted()
        //Assert
        Assert.assertNull(SUT.homeUiState.value.navigateToThisMovie)
    }

    private fun mockGetMoviesForRecommendedTests(list: List<Movie> = movieFakesForRecommendedCategory) {
        coEvery { getMoviesUseCase.invoke(any()) }.coAnswers {
            flow { emit(list) }
        }
    }
}