package brillembourg.parser.emovie.presentation.detail.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.getBackdropCompleteUrl
import coil.compose.AsyncImage
import coil.request.ImageRequest
import me.onebone.toolbar.*

@Composable
fun MovieCollapsableScaffold(
    modifier: Modifier = Modifier,
    movie: MoviePresentationModel,
    onClickBack: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val state = rememberCollapsingToolbarScaffoldState()
    CollapsingToolbarScaffold(
        modifier = modifier,
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            MovieCollapsableToolbar(state, movie, onClickBack)
        },
        toolbarModifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        content()
    }
}


@Composable
fun CollapsingToolbarScope.MovieCollapsableToolbar(
    state: CollapsingToolbarScaffoldState,
    movie: MoviePresentationModel,
    onClickBack: (() -> Unit)? = null,
) {
    val textSize = (18 + (30 - 12) * state.toolbarState.progress / 2).sp
    val initialPadding = (16 + 60f * (1f - state.toolbarState.progress)).dp

    Box(
        modifier = Modifier
//            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            .height(150.dp)
            .pin()
    )


    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(movie.getBackdropCompleteUrl())
            .crossfade(300)
            .build(),
        contentDescription = null,
        placeholder = painterResource(id = R.drawable.movie_placeholder),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .parallax()
            .height(500.dp),
        alpha = (state.toolbarState.progress)+0.25f,
        onError = {error -> Log.e("MovieCollapsable error",error.result.throwable.message.toString())}
    )

    Text(
        text = movie.name,
        style = TextStyle(color = Color.White, fontSize = textSize),
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .background(
                color = Color.Black.copy(alpha = 0.3f * state.toolbarState.progress),
                shape = RoundedCornerShape(topStartPercent = 30, topEndPercent = 30)
            )
            .padding(
                start = initialPadding,
                end = 16.dp,
                top = 24.dp,
                bottom = 24.dp
            )
            .road(
                whenCollapsed = Alignment.TopStart,
                whenExpanded = Alignment.BottomCenter
            ),
        textAlign = if (state.toolbarState.progress > 0.01f) TextAlign.Center  else TextAlign.Start,
    )

    IconButton(
        modifier = Modifier
            .padding(14.dp)
            .pin(),
        onClick = { onClickBack?.invoke() }) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
        )
    }
}