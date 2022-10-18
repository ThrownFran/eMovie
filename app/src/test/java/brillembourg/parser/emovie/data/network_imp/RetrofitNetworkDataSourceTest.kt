package brillembourg.parser.emovie.data.network_imp

import brillembourg.parser.emovie.data.MoviePage
import brillembourg.parser.emovie.data.network_imp.responses.*
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.utils.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RetrofitNetworkDataSourceTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var movieApi: MovieApi

    private lateinit var SUT: RetrofitNetworkDataSource

    private val trailerResponseMock = GetTrailersResponse().apply {
        this.results =
            (1..5).map { TrailerResponse(it.toString(), "key $it", "Name $it", "Site") }
    }

    private val movieResponseMock = GetMoviesResponse().apply {
        currentPage = 1
        lastPage = 3
        results = (1..10).map {
            MovieResponse(
                id = it.toLong(),
                name = "Movie name $it",
                posterImageUrl = "Poster",
                backDropImageUrl = "Backdrop",
                plot = "Plot $it",
                voteAverage = it.toFloat(),
                voteCount = it,
                originalLanguage = "es",
                releaseDate = "2000-11-05"
            )
        }
    }

    @Before
    fun setup() {
        SUT = RetrofitNetworkDataSource(movieApi)
    }

    @Test
    fun `get trailers, call api with correct params`() = runTest {
        //Arrange
        val movieId = 5L
        val apiKey = API_KEY
        mockMovieApiGetTrailersSuccess(movieId, apiKey)
        //Act
        val result = SUT.getTrailers(movieId)
        //Assert
        coVerify { movieApi.getTrailers(movieId, apiKey) }
    }

    @Test
    fun `get trailers, return response mapped in domain`() = runTest {
        //Arrange
        val movieId = 5L
        val apiKey = API_KEY
        mockMovieApiGetTrailersSuccess(movieId, apiKey)
        //Act
        val result = SUT.getTrailers(movieId)
        //Assert
        assertEquals(trailerResponseMock.results.map { it.toDomain(movieId) }, result)
    }

    @Test
    fun `get movies, when top rated category, call api with correct params`() = runTest {
        //Arrange
        val apiKey = API_KEY
        val page = 1
        coEvery {
            movieApi.getTopRated(apiKey,
                page)
        }.returns(movieResponseMock.apply { currentPage = page })
        //Act
        SUT.getMovies(Category.TopRated, page)
        //Assert
        coVerify { movieApi.getTopRated(apiKey, page) }
    }

    @Test
    fun `get movies, when upcoming category, call api with correct params`() = runTest {
        //Arrange
        val apiKey = API_KEY
        val page = 1
        coEvery {
            movieApi.getUpcoming(apiKey,
                page)
        }.returns(movieResponseMock.apply { currentPage = page })
        //Act
        SUT.getMovies(Category.Upcoming, page)
        //Assert
        coVerify { movieApi.getUpcoming(apiKey, page) }
    }

    @Test
    fun `get movies, when upcoming category, return domain model movie page response `() = runTest {
        //Arrange
        val apiKey = API_KEY
        val page = 1
        val lastPag = 3
        coEvery { movieApi.getUpcoming(apiKey, page) }.returns(movieResponseMock.apply {
            currentPage = page
            lastPage = lastPag
        })
        //Act
        val result = SUT.getMovies(Category.Upcoming, page)
        //Assert
        val expectedResult = MoviePage(
            movies = movieResponseMock.results?.map { it.toData() } ?: emptyList(),
            currentPage = page,
            lastPage = lastPag
        )
        Assert.assertEquals(expectedResult,result)
    }

    @Test
    fun `get movies, when top rated category, return domain model movie page response `() = runTest {
        //Arrange
        val apiKey = API_KEY
        val page = 1
        val lastPag = 3
        coEvery { movieApi.getTopRated(apiKey, page) }.returns(movieResponseMock.apply {
            currentPage = page
            lastPage = lastPag
        })
        //Act
        val result = SUT.getMovies(Category.TopRated, page)
        //Assert
        val expectedResult = MoviePage(
            movies = movieResponseMock.results?.map { it.toData() } ?: emptyList(),
            currentPage = page,
            lastPage = lastPag
        )
        Assert.assertEquals(expectedResult,result)
    }

    @Test
    fun `get movies, when results are null, return empty list` () = runTest {
        //Arrange
        coEvery { movieApi.getTopRated(any(),any()) }.returns(GetMoviesResponse())
        //Act
        val result = SUT.getMovies(Category.TopRated, 1)
        //Assert
        val expectedResult = MoviePage(
            movies = emptyList(),
            currentPage = 0,
            lastPage = 0
        )
        Assert.assertEquals(expectedResult,result)
    }

    private fun mockMovieApiGetTrailersSuccess(movieId: Long, apiKey: String) {
        coEvery { movieApi.getTrailers(movieId, apiKey) }.returns(trailerResponseMock)
    }

}