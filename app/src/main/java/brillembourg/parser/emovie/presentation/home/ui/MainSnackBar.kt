package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun MainSnackBar(messageToShow: String?, onMessageShown: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarHost(snackbarHostState)
    messageToShow?.let {
        LaunchedEffect(snackbarHostState) {
            val result = snackbarHostState.showSnackbar(it)
            if (result == SnackbarResult.Dismissed) {
                onMessageShown()
            }
        }
    }
}