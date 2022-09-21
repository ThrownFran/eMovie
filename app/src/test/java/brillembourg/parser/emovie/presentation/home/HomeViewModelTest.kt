package brillembourg.parser.emovie.presentation.home

import androidx.lifecycle.SavedStateHandle
import brillembourg.parser.emovie.core.NetworkException
import brillembourg.parser.emovie.data.PAGE_SIZE
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.use_cases.GetMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RefreshMoviesUseCase
import brillembourg.parser.emovie.domain.use_cases.RequestNextMoviePageUseCase
import brillembourg.parser.emovie.presentation.models.UiText
import brillembourg.parser.emovie.presentation.models.toPresentation
import brillembourg.parser.emovie.utils.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
        Movie(1L, "Movie1", null, "en", LocalDate.ofYearDay(1993, 1), "", "plot", 12, 4f),
        Movie(2L, "Movie2", null, "es", LocalDate.ofYearDay(1993, 1), "", "plot", 12, 4f),
        Movie(3L, "Movie3", null, "en", LocalDate.ofYearDay(1995, 1), "", "plot", 12, 4f),
        Movie(4L, "Movie4", null, "es", LocalDate.ofYearDay(1996, 1), "", "plot", 12, 4f),
        Movie(5L, "Movie5", null, "en", LocalDate.ofYearDay(1997, 1), "", "plot", 12, 4f),
        Movie(6L, "Movie6", null, "en", LocalDate.ofYearDay(1993, 1), "", "plot", 12, 4f),
        Movie(7L, "Movie7", null, "en", LocalDate.ofYearDay(1993, 1), "", "plot", 12, 4f),
        Movie(8L, "Movie8", null, "en", LocalDate.ofYearDay(1993, 1), "", "plot", 12, 4f),
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
    private lateinit var requestNextMoviePageUseCase: RequestNextMoviePageUseCase

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var SUT: HomeViewModel

    @Before
    fun setUp() {
    }

    private fun buildSUT() {
        SUT = HomeViewModel(
            savedStateHandle,
            getMoviesUseCase,
            refreshMoviesUseCase,
            requestNextMoviePageUseCase
        )
    }

    private fun mockGetMoviesSuccess() {
        coEvery { getMoviesUseCase.invoke(any()) }.coAnswers { flow { emit(movieDomainFakes) } }
        coEvery { refreshMoviesUseCase.invoke(any()) }.coAnswers {
            Unit
        }
        coEvery { requestNextMoviePageUseCase.invoke(any(),any()) }.coAnswers { Unit }
    }

    @Test
    fun `given init, then observe upcoming movies`() = runTest {
        //Arrange
        coEvery { getMoviesUseCase.invoke(any()) }.coAnswers { flow { emit(movieDomainFakes) } }
        buildSUT()
        //Act
        //Assert
        coVerify { getMoviesUseCase.invoke(match { params -> params is Category.Upcoming }) }
    }

    @Test
    fun `given init, then invoke refresh movie categories top rated and upcoming`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        //Assert
        coVerify(exactly = 1) { refreshMoviesUseCase.invoke(match { params -> params is Category.TopRated }) }
        coVerify(exactly = 1) { refreshMoviesUseCase.invoke(match { params -> params is Category.Upcoming }) }
    }

    @Test
    fun `given refresh movies data, when success, then show and hide loading state accordingly`() =
        runTest {
            //Arrange
            mockGetMoviesSuccess()
            coEvery { refreshMoviesUseCase.invoke(any()) }.coAnswers {
                delay(500)
                movieDomainFakes
            }
            buildSUT()
            //Assert
            advanceTimeBy(100)
            Assert.assertTrue(SUT.homeUiState.value.isLoading)
            advanceTimeBy(600)
            Assert.assertFalse(SUT.homeUiState.value.isLoading)
        }

    @Test
    fun `given refresh movies data, when error, then show user error and hide loading state`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        coEvery { refreshMoviesUseCase.invoke(any()) }.throws(NetworkException())
        buildSUT()
        //Act
        advanceUntilIdle()
        //Arrange
        Assert.assertEquals(UiText.NoInternet,SUT.homeUiState.value.messageToShow)
        Assert.assertFalse(SUT.homeUiState.value.isLoading)
    }

    @Test
    fun `given on swipe to refresh, then invoke refresh all movies categories`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        SUT.onRefresh()
        advanceUntilIdle()
        //Assert
        coVerify { refreshMoviesUseCase.invoke(ofType(Category.TopRated::class)) }
        coVerify { refreshMoviesUseCase.invoke(ofType(Category.Upcoming::class)) }
    }

    @Test
    fun `given on Message shown, then update message state to null`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        coEvery { refreshMoviesUseCase.invoke(any()) }.throws(NetworkException())
        buildSUT()
        //Act
        advanceUntilIdle()
        SUT.onMessageShown()
        //Arrange
        Assert.assertNull(SUT.homeUiState.value.messageToShow)
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
    fun `given top rated movies bottom reached, then request new page`() = runTest {
        //Arrange
        mockGetMoviesSuccess()
        buildSUT()
        //Act
        advanceUntilIdle()
        SUT.onEndOfTopRatedMoviesReached(PAGE_SIZE -1)
        advanceUntilIdle()
        //Assert
        coVerify { requestNextMoviePageUseCase.invoke(ofType(Category.TopRated::class), PAGE_SIZE - 1) }
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
                movieFakesForRecommendedCategory
                        + listOf(
                    MovieFake(9L).toDomain(),
                    MovieFake(10L).toDomain(),
                    MovieFake(11L).toDomain()
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
        val movieToBeFiltered = MovieFake(3L, LocalDate.ofYearDay(1993, 1)).toDomain()
        mockGetMoviesForRecommendedTests(
            listOf(
                MovieFake(1L, LocalDate.ofYearDay(2000, 1)).toDomain(),
                MovieFake(2L, LocalDate.ofYearDay(1990, 1)).toDomain(),
                movieToBeFiltered,
                MovieFake(4L, LocalDate.ofYearDay(2020, 1)).toDomain(),
                MovieFake(5L, LocalDate.ofYearDay(2001, 1)).toDomain(),
            )
        )
        buildSUT()
        //Act
        advanceUntilIdle()
        SUT.onYearFilterSelected(1993)
        //Arrange
        Assert.assertEquals(movieToBeFiltered.toPresentation().getReleaseYear(), SUT.homeUiState.value.recommendedMovies.yearFilter.currentYear)
        Assert.assertEquals(
            listOf(movieToBeFiltered.toPresentation()),
            SUT.homeUiState.value.recommendedMovies.movies
        )
    }

    @Test
    fun `given select language in recommended movies, then filter movie by language`() = runTest {
        //Arrange
        val movieInSpanish = MovieFake(id = 2L, language = "es").toDomain()
        mockGetMoviesForRecommendedTests(
            listOf(
                MovieFake(id = 1L, language = "en").toDomain(),
                movieInSpanish,
                MovieFake(id = 3L, language = "en").toDomain(),
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
                    MovieFake(
                        id = 1L,
                        language = "en",
                        date = LocalDate.ofYearDay(2010, 1)
                    ).toDomain(),
                    MovieFake(
                        id = 2L,
                        language = "es",
                        date = LocalDate.ofYearDay(2003, 1)
                    ).toDomain(),
                    MovieFake(
                        id = 3L,
                        language = "ja",
                        date = LocalDate.ofYearDay(1995, 1)
                    ).toDomain(),
                    MovieFake(
                        id = 4L,
                        language = "ja",
                        date = LocalDate.ofYearDay(2000, 1)
                    ).toDomain(),
                    MovieFake(
                        id = 5L,
                        language = "ja",
                        date = LocalDate.ofYearDay(1910, 1)
                    ).toDomain(),
                    MovieFake(
                        id = 6L,
                        language = "ja",
                        date = LocalDate.ofYearDay(1995, 1)
                    ).toDomain(),
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
            Assert.assertEquals(
                1995,
                SUT.homeUiState.value.recommendedMovies.yearFilter.currentYear
            )

            //Movies filtered
            Assert.assertEquals(
                listOf(
                    MovieFake(
                        id = 3L,
                        language = "ja",
                        date = LocalDate.ofYearDay(1995, 1)
                    ).toDomain().toPresentation(),
                    MovieFake(
                        id = 6L,
                        language = "ja",
                        date = LocalDate.ofYearDay(1995, 1)
                    ).toDomain().toPresentation()
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
        Assert.assertEquals(SUT.homeUiState.value.navigateToThisMovie, movieClicked)
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