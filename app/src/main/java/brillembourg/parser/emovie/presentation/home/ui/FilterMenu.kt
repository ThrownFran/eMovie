package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brillembourg.parser.emovie.presentation.theme.White
import brillembourg.parser.emovie.presentation.theme.eMovieTheme

val optionFakes = (1..10).map { "Option $it" }

@Composable
fun FilterMenu(
    modifier: Modifier = Modifier,
    currentOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentItem: String by rememberSaveable {
        mutableStateOf(currentOption)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            expanded = true
        }
    ) {

        Text(text = currentItem)

        Box(modifier = modifier) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                modifier = modifier.heightIn(max = 600.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(text = it)
                        },
                        onClick = {
                            currentItem = it
                            expanded = false
                        }
                    )
                }

            }
        }
    }

}

@Preview(showBackground = false, widthDp = 100, heightDp = 300)
@Composable
fun FilterMenuPreview() {
    eMovieTheme {
        Surface {
            FilterMenu(options = optionFakes, currentOption = optionFakes[0]) {

            }
        }
    }
}