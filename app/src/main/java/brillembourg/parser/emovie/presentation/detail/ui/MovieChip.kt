package brillembourg.parser.emovie.presentation.detail.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.presentation.theme.Shapes
import brillembourg.parser.emovie.presentation.theme.eMovieTheme

@Composable
fun MovieChip(
    modifier: Modifier = Modifier,
    text: String, imageVector: ImageVector? = null
) {
    OutlinedButton(shape = MaterialTheme.shapes.medium, onClick = {  }) {
        imageVector?.let { Icon(imageVector = it, contentDescription = text) }
        Spacer(modifier = modifier.width(4.dp))
        Text(text = text)
    }
}

@Preview
@Composable
fun MovieChipPreview() {
    eMovieTheme {
        MovieChip(
            text = "Chip 1", imageVector = Icons.Default.Star
        )
    }
}