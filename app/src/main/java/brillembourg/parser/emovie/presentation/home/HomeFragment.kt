@file:OptIn(ExperimentalCoroutinesApi::class)

package brillembourg.parser.emovie.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.core.Logger
import brillembourg.parser.emovie.databinding.FragmentHomeBinding
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.UiText
import brillembourg.parser.emovie.presentation.models.asString
import brillembourg.parser.emovie.presentation.utils.safeUiLaunch
import brillembourg.parser.emovie.presentation.utils.showMessage
import brillembourg.parser.emovie.presentation.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber

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
        setupRefreshListener()
        disableSwipeToRefreshIfExpanded()
    }

    private fun renderState() {
        safeUiLaunch {
            viewModel.homeUiState.collect { state ->

                renderTopRatedMovies(state.topRatedMovies)
                renderUpcomingMovies(state.upcomingMovies)
                renderRecommendedMovies(state.recommendedMovies)
                renderLoading(state.isLoading)

                handleNavigation(state.navigateToThisMovie)
                handleMessages(state.messageToShow)
            }
        }
    }

    private fun renderLoading(isLoading: Boolean) {
        binding.homeSwipeRefresh.isRefreshing = isLoading
    }

    private fun handleMessages(messageToShow: UiText?) {
        messageToShow?.let {
            showMessage(binding.homeCoordinator, messageToShow.asString(requireContext())) {
                viewModel.onMessageShown()
            }
        }
    }

    private fun handleNavigation(navigateToThisMovie: MoviePresentationModel?) {
        navigateToThisMovie?.let {
            clearFocus()
            setOriginSharedAxisTransition()
            val directions = HomeFragmentDirections.actionHomeFragmentToDetailFragment(navigateToThisMovie)
            findNavController().navigate(directions)
            viewModel.onNavigateToMovieCompleted()
        }
    }

    private fun clearFocus() {
        binding.homeTextinputYear.clearFocus()
        binding.homeTextinputLanguage.clearFocus()
    }

    private fun renderTopRatedMovies(topRatedMovies: List<MoviePresentationModel>) {
        binding.homeTextNoTopRated.isVisible = topRatedMovies.isEmpty()

        if (binding.homeRecyclerTopRated.adapter == null) {
            binding.homeRecyclerTopRated.apply {
                adapter = MovieAdapter { viewModel.onMovieClick(it) }
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                isNestedScrollingEnabled = false
            }


            safeUiLaunch {
                binding.homeRecyclerTopRated.lastVisibleEvents
                    .collectLatest { lastVisibleItem ->
                    viewModel.onEndOfTopRatedMoviesReached(lastVisibleItem)
                }
            }
        }

        (binding.homeRecyclerTopRated.adapter as? MovieAdapter?)?.submitList(topRatedMovies)
    }

    private fun renderUpcomingMovies(upcomingMovies: List<MoviePresentationModel>) {
        binding.homeTextNoUpcoming.isVisible = upcomingMovies.isEmpty()

        if (binding.homeRecyclerUpcoming.adapter == null) {
            binding.homeRecyclerUpcoming.apply {
                adapter = MovieAdapter { viewModel.onMovieClick(it) }
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                isNestedScrollingEnabled = false
            }
        }

        safeUiLaunch {
            binding.homeRecyclerUpcoming.lastVisibleEvents
                .collectLatest { lastVisibleItem ->
                    viewModel.onEndOfUpcomingMoviesReached(lastVisibleItem)
                }
        }

        (binding.homeRecyclerUpcoming.adapter as? MovieAdapter?)?.submitList(upcomingMovies)
    }

    private fun renderRecommendedMovies(recommendedMovies: RecommendedMovies) {
        binding.homeTextNoRecommended.isVisible = recommendedMovies.movies.isEmpty()
        renderRecommendedMovieRecycler(recommendedMovies.movies)
        renderYearFilter(recommendedMovies.yearFilter)
        renderLanguageFilter(recommendedMovies.languageFilter)
    }

    private fun renderRecommendedMovieRecycler(movies: List<MoviePresentationModel>) {
        if (binding.homeRecyclerRecommended.adapter == null) {
            binding.homeRecyclerRecommended.apply {
                adapter = MovieAdapter { viewModel.onMovieClick(it) }
                layoutManager = GridLayoutManager(context, 2,RecyclerView.VERTICAL,false)
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

    private fun setupRefreshListener() {
        binding.homeSwipeRefresh.setOnRefreshListener {
            viewModel.onRefresh()
        }
    }

    private fun disableSwipeToRefreshIfExpanded() {
        binding.mainAppbar.addOnOffsetChangedListener { _, verticalOffset ->
            try {
                binding.homeSwipeRefresh.isEnabled = verticalOffset == 0
            } catch (e: Exception) {
                Logger.error(e)
            }
        }
    }


}