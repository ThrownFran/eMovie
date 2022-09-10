package brillembourg.parser.emovie.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.databinding.ItemTrailerBinding
import brillembourg.parser.emovie.domain.models.Trailer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class TrailerAdapter(val lifecycleOwner: LifecycleOwner,
                     val onPlayerReady: () -> Unit,
                     val onPlaying: () -> Unit,
                     val onPause: () -> Unit,
                     val onWatchTrailer : (() -> Unit) -> Unit) :
    ListAdapter<Trailer, TrailerAdapter.TrailerViewHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        return TrailerViewHolder(
            ItemTrailerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class TrailerViewHolder(val binding: ItemTrailerBinding) : RecyclerView.ViewHolder(binding.root) {

        private var youTubePlayerView: YouTubePlayerView? = null
        private var youTubePlayer: YouTubePlayer? = null
        private var currentVideoId: String? = null
        private val tracker by lazy { YouTubePlayerTracker() }

        init {
            onWatchTrailer.invoke {
                if(tracker.state == PlayerConstants.PlayerState.PLAYING) {
                    youTubePlayer?.pause()
                } else {
                    youTubePlayer?.play()
                }
            }

            youTubePlayerView = binding.itemTrailerPagerYoutubeview
            lifecycleOwner.lifecycle.addObserver(binding.itemTrailerPagerYoutubeview)

            youTubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    this@TrailerViewHolder.youTubePlayer = youTubePlayer
                    this@TrailerViewHolder.youTubePlayer?.cueVideo(currentVideoId!!, 0f)
                    youTubePlayer.addListener(tracker)

                    youTubePlayer.addListener(object : YouTubePlayerListener {
                        override fun onStateChange(
                            youTubePlayer: YouTubePlayer,
                            state: PlayerConstants.PlayerState
                        ) {
                            if(tracker.state == PlayerConstants.PlayerState.PLAYING) {
                                onPlaying.invoke()
                            } else {
                                onPause.invoke()
                            }
                        }

                        override fun onApiChange(youTubePlayer: YouTubePlayer) {


                        }

                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {


                        }

                        override fun onError(
                            youTubePlayer: YouTubePlayer,
                            error: PlayerConstants.PlayerError
                        ) {


                        }

                        override fun onPlaybackQualityChange(
                            youTubePlayer: YouTubePlayer,
                            playbackQuality: PlayerConstants.PlaybackQuality
                        ) {


                        }

                        override fun onPlaybackRateChange(
                            youTubePlayer: YouTubePlayer,
                            playbackRate: PlayerConstants.PlaybackRate
                        ) {


                        }

                        override fun onReady(youTubePlayer: YouTubePlayer) {


                        }

                        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {

                        }

                        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {

                        }

                        override fun onVideoLoadedFraction(
                            youTubePlayer: YouTubePlayer,
                            loadedFraction: Float
                        ) {

                        }
                    });

                    onPlayerReady.invoke()
                }
            })



        }

        fun bind(trailer: Trailer) {
            currentVideoId = trailer.key;

            if(youTubePlayer == null)
                return;

            currentVideoId?.let { youTubePlayer?.cueVideo(it, 0f); }
        }

    }

}

val diffUtilCallback = object : DiffUtil.ItemCallback<Trailer>() {
    override fun areItemsTheSame(
        oldItem: Trailer,
        newItem: Trailer
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Trailer,
        newItem: Trailer
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: Trailer,
        newItem: Trailer
    ): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}