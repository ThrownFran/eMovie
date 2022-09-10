package brillembourg.parser.emovie.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.databinding.ItemTrailerBinding
import brillembourg.parser.emovie.domain.models.Trailer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class TrailerAdapter(val lifecycleOwner: LifecycleOwner,
                     val onClicked: (Trailer) -> Unit) :
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

        init {
            setupClickListener()

            youTubePlayerView = binding.itemTrailerPagerYoutubeview

            youTubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    this@TrailerViewHolder.youTubePlayer = youTubePlayer
                    this@TrailerViewHolder.youTubePlayer?.cueVideo(currentVideoId!!, 0f)
                }
            })
        }

        private fun setupClickListener() {
            itemView.setOnClickListener { onClicked.invoke(currentList[bindingAdapterPosition]) }
        }

        fun bind(trailer: Trailer) {
            currentVideoId = trailer.key;

            if(youTubePlayer == null)
                return;

            currentVideoId?.let { youTubePlayer?.cueVideo("S0Q4gqBUs7c", 0f); }
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