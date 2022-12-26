package brillembourg.parser.emovie.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import brillembourg.parser.emovie.presentation.detail.ui.DetailContent
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import brillembourg.parser.emovie.presentation.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDestinationSharedAxisTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                eMovieTheme {
                    val uiState = viewModel.detailUiState.collectAsState()
                    DetailContent(
                        uiState = uiState.value,
                        lifecycleOwner = viewLifecycleOwner,
                        onClickBack = {
                            findNavController().navigateUp()
                        },
                        onRefresh = {
                            viewModel.onRefresh()
                        },
                        onMessageShown = {
                            viewModel.onMessageShown()
                        })
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareTransition(view)
    }


}