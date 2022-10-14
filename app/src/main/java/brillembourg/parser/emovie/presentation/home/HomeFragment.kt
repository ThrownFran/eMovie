@file:OptIn(ExperimentalMaterial3Api::class)

package brillembourg.parser.emovie.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import brillembourg.parser.emovie.presentation.home.ui.HomeScreen
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.asString
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import brillembourg.parser.emovie.presentation.utils.setOriginSharedAxisTransition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                eMovieTheme {
                    val uiState = viewModel.homeUiState.collectAsState()
                    HomeScreen(
                        upcomingMovies = uiState.value.upcomingMovies,
                        isLoadingMoreUpcomingMovies = uiState.value.isLoadingMoreUpcomingMovies,
                        onEndReachedForUpcomingMovies = { viewModel.onEndOfUpcomingMoviesReached(it) },
                        topRatedMovies = uiState.value.topRatedMovies,
                        isLoadingMoreTopRatedMovies = uiState.value.isLoadingMoreTopRatedMovies,
                        onEndReachedForTopRatedMovies = { viewModel.onEndOfTopRatedMoviesReached(it) },
                        recommendedMovies = uiState.value.recommendedMovies,
                        onFilterYearChanged = viewModel::onYearFilterSelected,
                        onFilterLanguageChanged = viewModel::onLanguageFilterSelected,
                        messageToShow = uiState.value.messageToShow?.asString(context),
                        onMessageShown = viewModel::onMessageShown,
                        isLoading = uiState.value.isLoading,
                        onRefresh = viewModel::onRefresh,
                        onMovieClick = {
                            handleNavigation(it)
                        }
                    )
                }
            }
        }
    }

    private fun handleNavigation(navigateToThisMovie: MoviePresentationModel?) {
        navigateToThisMovie?.let {
            setOriginSharedAxisTransition()
            val directions = HomeFragmentDirections.actionHomeFragmentToDetailFragment(navigateToThisMovie)
            findNavController().navigate(directions)
//            viewModel.onNavigateToMovieCompleted()
        }
    }

}