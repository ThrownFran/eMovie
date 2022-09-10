package brillembourg.parser.emovie.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.databinding.FragmentHomeBinding
import brillembourg.parser.emovie.presentation.detail.DetailFragmentArgs
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
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
        if(_binding == null) {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderState()
    }

    private fun renderState() {
        safeUiLaunch {
            viewModel.homeUiState.collect { state ->
                renderTopRatedMovies(state.topRatedMovies)
                renderUpcomingMovies(state.upcomingMovies)
                renderRecommendedMovies(state.recommendedMovies)
                handleNavigation(state.navigateToThisMovie)
            }
        }
    }

    private fun handleNavigation(navigateToThisMovie: MoviePresentationModel?) {
        navigateToThisMovie?.let {
            findNavController().navigate(
                resId = R.id.DetailFragment,
                args = Bundle().apply { putParcelable("movie", navigateToThisMovie) }
            )
            viewModel.onNavigateToMovieCompleted()
        }
    }

    private fun renderTopRatedMovies(topRatedMovies: List<MoviePresentationModel>) {
        if (binding.homeRecyclerTopRated.adapter == null) {
            binding.homeRecyclerTopRated.apply {
                adapter = MovieAdapter {
                    viewModel.onMovieClick(it)
                }
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                isNestedScrollingEnabled = false
            }
        }

        (binding.homeRecyclerTopRated.adapter as? MovieAdapter?)?.submitList(topRatedMovies)
    }

    private fun renderUpcomingMovies(upcomingMovies: List<MoviePresentationModel>) {
        if (binding.homeRecyclerUpcoming.adapter == null) {
            binding.homeRecyclerUpcoming.apply {
                adapter = MovieAdapter {
                    viewModel.onMovieClick(it)
                }
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                isNestedScrollingEnabled = false
            }
        }

        (binding.homeRecyclerUpcoming.adapter as? MovieAdapter?)?.submitList(upcomingMovies)
    }

    private fun renderRecommendedMovies(recommendedMovies: RecommendedMovies) {
        renderRecommendedMovieRecycler(recommendedMovies.movies)
        renderYearFilter(recommendedMovies.yearFilter)
        renderLanguageFilter(recommendedMovies.languageFilter)
    }

    private fun renderRecommendedMovieRecycler(movies: List<MoviePresentationModel>) {
        if (binding.homeRecyclerRecommended.adapter == null) {
            binding.homeRecyclerRecommended.apply {
                adapter = MovieAdapter {
                    viewModel.onMovieClick(it)
                }
                layoutManager = GridLayoutManager(context, 3)
                isNestedScrollingEnabled = false
            }
        }

        (binding.homeRecyclerRecommended.adapter as? MovieAdapter?)?.submitList(movies)
    }

    private fun renderLanguageFilter(filter: LanguageFilter) {
        val allLanguagesText = getString(R.string.all_languages)
        binding.homeAutotextLanguage.apply {
            setText(filter.currentLanguage ?: allLanguagesText)
            setAdapter(
                buildSpinnerAdapter(
                    allOptionsStringValue = allLanguagesText,
                    optionList = filter.selectableLanguages
                )
            )
            binding.homeAutotextLanguage.setOnItemClickListener { adapterView, _, i, _ ->
                val itemSelected = adapterView.getItemAtPosition(i) as String
                if (itemSelected == allLanguagesText) viewModel.onSetNoLanguageFilter()
                else viewModel.onLanguageFilterSelected(itemSelected)
            }
        }
    }


    private fun renderYearFilter(yearFilter: YearFilter) {
        val allYears = getString(R.string.all_years)

        binding.homeAutotextYear.apply {
            setText(yearFilter.currentYear?.toString() ?: allYears)

            setAdapter(buildSpinnerAdapter(
                allOptionsStringValue = allYears,
                optionList = yearFilter.selectableYears.map { it.toString() }
            ))

            setOnItemClickListener { adapterView, _, i, _ ->
                val itemSelected = adapterView.getItemAtPosition(i) as String

                if (itemSelected == allYears) viewModel.onSetNoYearFilter()
                else viewModel.onYearFilterSelected(itemSelected.toInt())
            }
        }
    }

    private fun buildSpinnerAdapter(
        allOptionsStringValue: String,
        optionList: List<String>
    ) = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_dropdown_item,
        listOf(allOptionsStringValue) + optionList
    )


    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}