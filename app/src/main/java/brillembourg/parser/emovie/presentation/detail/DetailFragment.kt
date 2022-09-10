package brillembourg.parser.emovie.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.databinding.FragmentDetailBinding
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.presentation.safeUiLaunch
import brillembourg.parser.emovie.presentation.utils.CircleDotsDecorator
import brillembourg.parser.emovie.presentation.utils.ImageType
import brillembourg.parser.emovie.presentation.utils.setMovieDbImageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderState()
        setNavigationListener()
    }

    private fun renderState() {
        safeUiLaunch {
            viewModel.detailUiState.collect { state ->

                renderBackgroundImage(state.movie.backdropImageUrl)
                renderName(state.movie.name)
                renderYear(state.movie.getReleaseYear())
                renderLanguage(state.movie.originalLanguage)
                renderVotes(state.movie.voteAverage)
                renderPlot(state.movie.plot)
                renderTrailers(state.trailers)
            }
        }
    }

    private fun renderTrailers(trailers: List<Trailer>) {

        if (trailers.isEmpty()) return


        if (binding.detailRecyclerTrailers.adapter == null) {
            binding.detailRecyclerTrailers.apply {
                adapter = TrailerAdapter(
                    viewLifecycleOwner,
                    onPlayerReady = {
//                        binding.detailRecyclerTrailers.bac
                    },
                    onWatchTrailer = { event ->
                        binding.detailButtonWatchTrailer.setOnClickListener {
                            event.invoke()
                        }
                    },
                    onPlaying = {
                        binding.detailButtonWatchTrailer.text =
                            getString(R.string.stop_trailer)
                    },
                    onPause = {
                        binding.detailButtonWatchTrailer.text =
                            getString(R.string.watch_trailer)
                    }
                )
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                isNestedScrollingEnabled = false
            }
        }

        (binding.detailRecyclerTrailers.adapter as TrailerAdapter).submitList(trailers.take(1))
    }

    private fun renderPlot(plot: String) {
        binding.detailTextPlot.text = plot
    }

    private fun renderVotes(voteAverage: Float) {
        val voteRounded = (voteAverage * 100).roundToInt().toDouble() / 100
        binding.detailChipVoteAverage.text = voteRounded.toString()
    }

    private fun renderLanguage(language: String) {
        binding.detailChipLanguage.text = language
    }

    private fun renderYear(releaseYear: Int) {
        binding.detailChipYear.text = releaseYear.toString()
    }

    private fun renderName(name: String) {
        binding.detailCollapsingToolbar.title = name
    }

    private fun renderBackgroundImage(backdropImageUrl: String?) {
        backdropImageUrl?.let {
            binding.detailImageBackdrop.setMovieDbImageUrl(it, ImageType.Backdrop)
        }
    }

    private fun setNavigationListener() {
        binding.detailToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}