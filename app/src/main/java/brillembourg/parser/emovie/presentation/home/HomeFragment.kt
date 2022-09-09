package brillembourg.parser.emovie.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.databinding.FragmentHomeBinding
import brillembourg.parser.emovie.presentation.MoviePresentationModel
import brillembourg.parser.emovie.presentation.safeUiLaunch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel : HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderState()
    }

    private fun renderState() {
        safeUiLaunch {
            viewModel.homeState.collect { state ->
                renderTopRatedMovies(state.topRatedMovies)
                renderUpcomingMovies(state.upcomingMovies)
            }
        }
    }

    private fun renderUpcomingMovies(upcomingMovies: List<MoviePresentationModel>) {
        if(binding.homeRecyclerUpcoming.adapter == null) {
            binding.homeRecyclerUpcoming.apply {
                adapter = MovieAdapter()
                layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
                isNestedScrollingEnabled = false
            }
        }

        (binding.homeRecyclerUpcoming.adapter as? MovieAdapter?)?.submitList(upcomingMovies)
    }

    private fun renderTopRatedMovies(topRatedMovies: List<MoviePresentationModel>) {
        if(binding.homeRecyclerTopRated.adapter == null) {
            binding.homeRecyclerTopRated.apply {
                adapter = MovieAdapter()
                layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
                isNestedScrollingEnabled = false
            }
        }

        (binding.homeRecyclerTopRated.adapter as? MovieAdapter?)?.submitList(topRatedMovies)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}