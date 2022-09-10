package brillembourg.parser.emovie.presentation.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.databinding.FragmentDetailBinding
import brillembourg.parser.emovie.presentation.safeUiLaunch
import brillembourg.parser.emovie.presentation.setMovieDbImageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.pow
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

                state.movie.backdropImageUrl?.let {
                    binding.detailImageBackdrop.setMovieDbImageUrl(it)
                }

                binding.detailCollapsingToolbar.title = state.movie.name
                binding.detailChipYear.text = state.movie.getReleaseYear().toString()
                binding.detailChipLanguage.text = state.movie.originalLanguage

                val voteRounded = (state.movie.voteAverage * 100).roundToInt().toDouble() / 100
                binding.detailChipVoteAverage.text = voteRounded.toString()
                binding.detailTextPlot.text = state.movie.plot

            }
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