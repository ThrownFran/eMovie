package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
    isLoadingMore: Boolean = false,
    onMovieClick: ((MoviePresentationModel) -> Unit)? = null,
    onEndReached: (position: Int) -> Unit,
) {
    val state = rememberLazyListState()

    Box {
        LazyRow(
            state = state,
            modifier = modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {

            itemsIndexed(movies, key = { i,item -> item.id }) { i,movie ->
                if(i >= movies.size -5) {
                    onEndReached.invoke(movies.size - 1)
                }
                MovieItem(movie = movie, onClick = { onMovieClick?.invoke(movie) })
            }

            if(isLoadingMore) {
                item {
                    Column(modifier = Modifier
                        .padding(16.dp)
                        .width(136.dp)
                        .height(192.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(CenterHorizontally)
                        )
                    }
                }
            }

//            if (isLoadingMore) {
//                item {
//                    Box(modifier = Modifier
//                        .padding(16.dp)
//                        .width(120.dp)
//                        .align(Alignment.CenterEnd),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//            }

        }

//        if (isLoadingMore) {
//            Box(modifier = Modifier
//                .padding(16.dp)
//                .align(Alignment.CenterEnd),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
    }

//    val isEndReached = remember {
//        derivedStateOf {
//            val lastVisibleItemIndex = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index?:0
//            val totalItems = state.layoutInfo.totalItemsCount
//            lastVisibleItemIndex >= totalItems - 10
////            state.layoutInfo.visibleItemsInfo
////                .lastOrNull()?.index == state.layoutInfo.totalItemsCount - 1
//        }
//    }
//
////    LaunchedEffect(true) {
//    if (isEndReached.value) {
//        onEndReached.invoke(state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)
//    }
//    }


}

@Preview(showBackground = false)
@Composable
fun MovieRowItemsPreview() {
    eMovieTheme {
        MovieRowItems(movies = movieListFake, onEndReached = {}, isLoadingMore = true)
    }
}