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
import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.presentation.home.ui.HomeContent
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.asString
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                eMovieTheme {
                    val uiState = viewModel.homeUiState.collectAsState()
                    val recommendedMovies = viewModel.recommendedMovies.collectAsState()
                    HomeContent(
                        upcomingMovies = uiState.value.upcomingMovies,
                        isLoadingMoreUpcomingMovies = uiState.value.isLoadingMoreUpcomingMovies,
                        onEndReachedForUpcomingMovies = { lastVisibleItem ->
                            viewModel.onAction.invoke(HomeViewModel.UiAction.EndOfPageReached(
                                Category.Upcoming,
                                lastVisibleItem))
                        },
                        isLastUpcomingPageReached = uiState.value.isLastUpcomingPageReached,

                        topRatedMovies = uiState.value.topRatedMovies,
                        isLoadingMoreTopRatedMovies = uiState.value.isLoadingMoreTopRatedMovies,
                        onEndReachedForTopRatedMovies = { lastVisibleItem ->
                            viewModel.onAction.invoke(HomeViewModel.UiAction.EndOfPageReached(
                                Category.TopRated,
                                lastVisibleItem))
                        },
                        isLastTopRatedPageReached = uiState.value.isLastTopRatedPageReached,

                        recommendedMovies = recommendedMovies.value,
                        onFilterYearChanged = {
                            viewModel.onAction(HomeViewModel.UiAction.SelectYear(it))
                        },
                        onFilterLanguageChanged = {
                            viewModel.onAction(HomeViewModel.UiAction.SelectLanguage(it))
                        },
                        messageToShow = uiState.value.messageToShow?.asString(context),
                        onMessageShown = viewModel::onMessageShown,
                        isLoading = uiState.value.isLoading,
                        onRefresh = {
                            viewModel.onAction(HomeViewModel.UiAction.Refresh)
                        },
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
//            setOriginSharedAxisTransition()
            val directions =
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(navigateToThisMovie)
            findNavController().navigate(directions)
//            viewModel.onNavigateToMovieCompleted()
        }
    }

}