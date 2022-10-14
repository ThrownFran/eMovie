package brillembourg.parser.emovie.presentation.detail.ui

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.data.local_imp.trailers.MovieWithTrailers
import brillembourg.parser.emovie.domain.models.Trailer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun MovieTrailer(modifier: Modifier = Modifier, lifecycleOwner: LifecycleOwner, trailers: List<Trailer>) {

    val player: MutableState<YouTubePlayer?> = remember {
        mutableStateOf(null)
    }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.movie_trailer),
            style = MaterialTheme.typography.headlineSmall
        )

        AndroidView(
            factory = { context ->
                val youTubePlayerView = YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false
                }
                youTubePlayerView // return the view
            },
            update = { view ->
                // Update the view
                if (trailers.isEmpty()) return@AndroidView

                val trailer = trailers.first()

                lifecycleOwner.lifecycle.addObserver(view)

                val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
                    .controls(0)
                    .rel(0)
                    .ivLoadPolicy(0)
                    .ccLoadPolicy(0)
                    .build()

                if (player.value == null) {
                    view.initialize(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            super.onReady(youTubePlayer)
                            player.value = youTubePlayer
//                        binding.itemTrailerFrameShadow.isVisible = false
                            player.value?.cueVideo(trailer.key, 0f)
                        }
                    }, iFramePlayerOptions)
                } else {
                    player.value?.cueVideo(trailer.key, 0f)
                }
            }
        )
    }
}