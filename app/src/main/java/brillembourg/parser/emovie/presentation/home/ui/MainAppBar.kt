@file:OptIn(ExperimentalMaterial3Api::class)

package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.theme.eMovieTheme

@Composable
fun MainAppBar() {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_name),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Preview
@Composable
fun MainAppBarPreview () {
    eMovieTheme {
        MainAppBar()
    }
}