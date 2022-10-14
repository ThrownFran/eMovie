package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

val movieListFake = (1..10).map {
    MoviePresentationModel(
        it.toLong(),
        "Name $it",
        "https://developer.android.com/static/images/home/billboard-compose-camp.png",
        "en",
        LocalDate.ofEpochDay(1000),
        "",
        "",
        4,
        6f
    )
}

@Composable
fun MovieRowItems(
    modifier: Modifier = Modifier,
    movies: List<MoviePresentationModel>,
    isLoadingMoreItems: Boolean = false,
    isLastPageReached: Boolean = false,
    onMovieClick: ((MoviePresentationModel) -> Unit)? = null,
    onEndOfPageReached: (position: Int) -> Unit,
) {
    val state = rememberLazyListState()

    val isEndOfPageReached = remember {
        derivedStateOf {
            val lastVisibleItemIndex = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = state.layoutInfo.totalItemsCount
            (lastVisibleItemIndex == totalItems - 1)
        }
    }

    if (isEndOfPageReached.value && movies.isNotEmpty() && !isLoadingMoreItems && !isLastPageReached) {
        onEndOfPageReached.invoke((state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) - 1)
    }

    Box {
        LazyRow(
            state = state,
            modifier = modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {


            itemsIndexed(movies, key = { i, item -> item.id }) { i, movie ->

//                if (isEndOfPageReached.value && i >= movies.size - 1) {
//                    onEndOfPageReached.invoke(i)
//                }

                MovieItem(movie = movie, onClick = {
                    onMovieClick?.invoke(movie)
                })
            }

            if (movies.isNotEmpty() && !isLastPageReached) {
                item {
                    Column(modifier = Modifier
                        .width(136.dp)
                        .height(192.dp)
                        .align(Alignment.CenterEnd),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun MovieRowItemsPreview() {
    eMovieTheme {

        val isEmpty = false
        val movies = if (!isEmpty) movieListFake.take(2) else emptyList()
        val moviesState = remember { mutableStateOf(movies) }
        val isLoadingMore = remember {
            mutableStateOf(false)
        }
        val isLastPageReached = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        MovieRowItems(
            movies = moviesState.value,
            isLastPageReached = isLastPageReached.value,
            onEndOfPageReached = {
                coroutineScope.launch {
                    delay(200)
                    isLoadingMore.value = true
                    moviesState.value =
                        moviesState.value + (30..35).map { movieFake.copy(id = it.toLong()) }
                    isLoadingMore.value = false
                    isLastPageReached.value = true
                }
            },
            isLoadingMoreItems = isLoadingMore.value,
        )
    }
}