package brillembourg.parser.emovie.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.databinding.ItemMovieBinding
import brillembourg.parser.emovie.presentation.MoviePresentationModel
import brillembourg.parser.emovie.presentation.setMovieDbImageUrl

class MovieAdapter() :
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

    class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MoviePresentationModel) {
            movie.posterImageUrl?.let { binding.movieImage.setMovieDbImageUrl(it) }
            binding.movieYear.text = buildString {
                append(movie.getReleaseYear())
                append(" (")
                append(movie.originalLanguage)
                    .append(")")
            }
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

    override fun getChangePayload(
        oldItem: MoviePresentationModel,
        newItem: MoviePresentationModel
    ): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}