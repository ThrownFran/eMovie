package brillembourg.parser.emovie.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class TrailerAdapter(
    val lifecycleOwner: LifecycleOwner,
) :
    ListAdapter<Trailer, TrailerAdapter.TrailerViewHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        return TrailerViewHolder(
            ItemTrailerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class TrailerViewHolder(val binding: ItemTrailerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var youTubePlayerView: YouTubePlayerView? = null
        private var youTubePlayer: YouTubePlayer? = null
        private var currentVideoId: String? = null

        init {
            binding.itemTrailerFrameShadow.isVisible = true
            youTubePlayerView = binding.itemTrailerPagerYoutubeview
            lifecycleOwner.lifecycle.addObserver(binding.itemTrailerPagerYoutubeview)

            val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
                .controls(0)
                .rel(0)
                .ivLoadPolicy(0)
                .ccLoadPolicy(0)
                .build()

            youTubePlayerView?.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    binding.itemTrailerFrameShadow.isVisible = false
                    this@TrailerViewHolder.youTubePlayer = youTubePlayer
                    this@TrailerViewHolder.youTubePlayer?.cueVideo(currentVideoId!!, 0f)
                }
            }, iFramePlayerOptions)

        }

        fun bind(trailer: Trailer) {
            currentVideoId = trailer.key;

            if (youTubePlayer == null)
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