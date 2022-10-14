package brillembourg.parser.emovie.presentation.home.ui

import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.presentation.theme.eMovieTheme

@Composable
fun HomeSection(
    title: String,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        content?.invoke()
        content?.let {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 230, heightDp = 560)
@Composable
fun HomeSectionPreview() {
    eMovieTheme {
        Surface {
            Column {
                HomeSection(title = "Title large") {

                }
                HomeSection(title = "Title large 2") {

                }
            }
        }
    }
}