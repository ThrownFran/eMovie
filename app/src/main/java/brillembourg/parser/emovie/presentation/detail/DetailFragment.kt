package brillembourg.parser.emovie.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import brillembourg.parser.emovie.databinding.FragmentDetailBinding
import brillembourg.parser.emovie.domain.models.Trailer
import brillembourg.parser.emovie.presentation.safeUiLaunch
import brillembourg.parser.emovie.presentation.showMessage
import brillembourg.parser.emovie.presentation.utils.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
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

    private var  player: YouTubePlayer? = null

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
        setupSwipeRefreshListener()
        disableSwipeToRefreshIfExpanded()
    }

    private fun disableSwipeToRefreshIfExpanded() {
        binding.detailAppbar.addOnOffsetChangedListener { _, verticalOffset ->
            try {
                binding.detailSwipeRefresh.isEnabled = verticalOffset == 0
            } catch (e: Exception) {
                Logger.error(e)
            }
        }
    }

    private fun setupSwipeRefreshListener() {
        binding.detailSwipeRefresh.setOnRefreshListener {
            viewModel.onRefresh()
        }
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
                renderTrailer(state.trailers)
                renderPoster(state.movie.posterImageUrl)
                handleMessage(state.messageToShow)
                renderSwipeLoadingState(state.isLoading)
            }
        }
    }

    private fun renderPoster(posterImageUrl: String?) {
        posterImageUrl?.let { binding.detailImagePoster.setMovieDbImageUrl(it,ImageType.Poster) }
    }

    private fun renderSwipeLoadingState(isLoading: Boolean) {
        binding.detailSwipeRefresh.isRefreshing = isLoading
    }

    private fun handleMessage(messageToShow: UiText?) {
        messageToShow?.let {
            showMessage(binding.detailCoordinator, it.asString(requireContext())) {
                viewModel.onMessageShown()
            }
        }
    }

    private fun renderTrailer(trailers: List<Trailer>) {

        if(trailers.isEmpty()) return

        val trailer = trailers.first()

        viewLifecycleOwner.lifecycle.addObserver(binding.detailYoutubePlayer)

        val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(0)
            .rel(0)
            .ivLoadPolicy(0)
            .ccLoadPolicy(0)
            .build()

        if(player == null) {
            binding.detailYoutubePlayer.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    player = youTubePlayer
                    binding.itemTrailerFrameShadow.isVisible = false
                    player?.cueVideo(trailer.key,0f)
                }
            }, iFramePlayerOptions)
        } else {
            player?.cueVideo(trailer.key,0f)
        }
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