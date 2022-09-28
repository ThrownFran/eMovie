@file:OptIn(ExperimentalMaterial3Api::class)

package brillembourg.parser.emovie.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.core.Logger
import brillembourg.parser.emovie.databinding.FragmentHomeBinding
import brillembourg.parser.emovie.presentation.home.ui.*
import brillembourg.parser.emovie.presentation.models.MoviePresentationModel
import brillembourg.parser.emovie.presentation.models.UiText
import brillembourg.parser.emovie.presentation.models.asString
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import brillembourg.parser.emovie.presentation.utils.safeUiLaunch
import brillembourg.parser.emovie.presentation.utils.showMessage
import brillembourg.parser.emovie.presentation.utils.*
import com.google.android.material.composethemeadapter3.Mdc3Theme
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
                        topRatedMovies = uiState.value.topRatedMovies,
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