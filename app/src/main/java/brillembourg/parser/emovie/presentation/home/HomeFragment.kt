package brillembourg.parser.emovie.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.databinding.FragmentHomeBinding
import brillembourg.parser.emovie.presentation.MoviePresentationModel
import brillembourg.parser.emovie.presentation.safeUiLaunch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

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
                renderRecommendedMovies(state.recommendedMovies)
            }
        }
    }

    private fun renderRecommendedMovies(recommendedMovies: RecommendedMovies) {
        renderRecommendedMovieRecycler(recommendedMovies)
        renderYearFilter(recommendedMovies)

        val allLanguagesText = getString(R.string.all_languages)
        binding.homeAutotextLanguage.apply {
            setText(recommendedMovies.languageFilter.currentLanguage ?: allLanguagesText)
            setAdapter(ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                listOf(allLanguagesText) + recommendedMovies.languageFilter.selectableLanguages
            ))
            binding.homeAutotextLanguage.setOnItemClickListener { adapterView, view, i, l ->
                val itemSelected = adapterView.getItemAtPosition(i) as String
                if (itemSelected == allLanguagesText) viewModel.onSetNoLanguageFilter()
                else viewModel.onLanguageFilterSelected(itemSelected)
            }
        }

    }

    private fun renderRecommendedMovieRecycler(recommendedMovies: RecommendedMovies) {
        if (binding.homeRecyclerRecommended.adapter == null) {
            binding.homeRecyclerRecommended.apply {
                adapter = MovieAdapter()
                layoutManager = GridLayoutManager(context, 3)
                isNestedScrollingEnabled = false
            }
        }

        (binding.homeRecyclerRecommended.adapter as? MovieAdapter?)?.submitList(recommendedMovies.movies)
    }

    private fun renderYearFilter(recommendedMovies: RecommendedMovies) {
        val allYears = getString(R.string.all_years)

        val yearAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf(allYears) + recommendedMovies.yearFilter.selectableYears.map { it.toString() }
        )

        binding.homeAutotextYear.apply {
            setText(recommendedMovies.yearFilter.currentYear?.toString() ?: allYears)
            setAdapter(yearAdapter)
            setOnItemClickListener { adapterView, view, i, l ->
                val itemSelected = adapterView.getItemAtPosition(i) as String

                if (itemSelected == allYears) viewModel.onSetNoYearFilter()
                else viewModel.onYearFilterSelected(itemSelected.toInt())
            }
        }
    }

    private fun renderUpcomingMovies(upcomingMovies: List<MoviePresentationModel>) {
        if (binding.homeRecyclerUpcoming.adapter == null) {
            binding.homeRecyclerUpcoming.apply {
                adapter = MovieAdapter()
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                isNestedScrollingEnabled = false
            }
        }

        (binding.homeRecyclerUpcoming.adapter as? MovieAdapter?)?.submitList(upcomingMovies)
    }

    private fun renderTopRatedMovies(topRatedMovies: List<MoviePresentationModel>) {
        if (binding.homeRecyclerTopRated.adapter == null) {
            binding.homeRecyclerTopRated.apply {
                adapter = MovieAdapter()
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
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