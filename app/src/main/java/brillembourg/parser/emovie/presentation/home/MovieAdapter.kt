package brillembourg.parser.emovie.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.databinding.ItemMovieBinding
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.utils.ImageType
import brillembourg.parser.emovie.presentation.utils.setMovieDbImageUrl

class MovieAdapter(val onClicked: (MoviePresentationModel) -> Unit) :
    ListAdapter<MoviePresentationModel, MovieAdapter.MovieViewHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupClickListener()
        }

        private fun setupClickListener() {
            itemView.setOnClickListener { onClicked.invoke(currentList[bindingAdapterPosition]) }
        }

        fun bind(movie: MoviePresentationModel) {
            movie.posterImageUrl?.let { binding.movieImage.setMovieDbImageUrl(it, ImageType.Poster) }
        }

    }

}

val diffUtilCallback = object : DiffUtil.ItemCallback<MoviePresentationModel>() {
    override fun areItemsTheSame(
        oldItem: MoviePresentationModel,
        newItem: MoviePresentationModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MoviePresentationModel,
        newItem: MoviePresentationModel
    ): Boolean {
        return oldItem == newItem
    }
}