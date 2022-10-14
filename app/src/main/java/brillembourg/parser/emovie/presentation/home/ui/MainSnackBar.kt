package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun MainSnackBar(
    messageToShow: String?,
    onMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(
        key1 = messageToShow,
        key2 = snackbarHostState
    ) {
        messageToShow?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    SnackbarHost(snackbarHostState)
}