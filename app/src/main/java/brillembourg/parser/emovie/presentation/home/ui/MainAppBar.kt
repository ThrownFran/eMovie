@file:OptIn(ExperimentalMaterial3Api::class)

package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.theme.White
import brillembourg.parser.emovie.presentation.theme.eMovieTheme

@Composable
fun MainAppBar() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .background(MaterialTheme.colorScheme.surface)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.app_name),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(CenterVertically).size(60.dp)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.align(CenterVertically),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.SansSerif
        )
    }

//    TopAppBar(
//        title = {
////            Row(verticalAlignment = Alignment.CenterVertically) {
////                Icon(
////                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
////                    contentDescription = stringResource(id = R.string.app_name),
////                    tint = MaterialTheme.colorScheme.primary,
////                    modifier = Modifier.align(CenterVertically)
////                )
//            Text(text = stringResource(id = R.string.app_name))
////            }
//        },
//        navigationIcon = {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                contentDescription = stringResource(id = R.string.app_name),
//                tint = MaterialTheme.colorScheme.primary
//            )
//        },
//        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Gray),
//    )
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun MainAppBarPreview() {
    eMovieTheme {
        Surface {
            MainAppBar()
        }
    }
}