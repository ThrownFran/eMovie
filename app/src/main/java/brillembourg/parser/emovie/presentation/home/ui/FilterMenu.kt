@file:OptIn(ExperimentalMaterial3Api::class)

package brillembourg.parser.emovie.presentation.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brillembourg.parser.emovie.presentation.theme.White
import brillembourg.parser.emovie.presentation.theme.eMovieTheme
import kotlin.math.exp


val optionFakes = (1..10).map { "Option $it" }

@Composable
fun FilterMenu(
    modifier: Modifier = Modifier,
    label: String,
    currentOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var currentItem: String by rememberSaveable {
        mutableStateOf(currentOption)
    }
    var dropDownWidth by rememberSaveable {
        mutableStateOf(0)
    }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.widthIn(max = 180.dp)) {
        OutlinedTextField(
            value = currentItem,
            onValueChange = {},
            modifier = Modifier
                .defaultMinSize(minWidth = 100.dp)
                .onSizeChanged {
                    dropDownWidth = it.width
                }
                .clickable { expanded = !expanded }
                .onFocusChanged {
                    expanded = it.isFocused
                },
            label = { Text(label) },
            trailingIcon = {
                Icon(imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "contentDescription",
                    modifier = Modifier.clickable { expanded = !expanded })
            },
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                focusManager.clearFocus()
            },
            modifier = Modifier
                .heightIn(max = 300.dp)
                .width(with(LocalDensity.current) { dropDownWidth.toDp() })
        ) {
            options.forEach { label ->
                DropdownMenuItem(onClick = {
                    currentItem = label
                    onOptionSelected.invoke(currentItem)
                    expanded = false
                    focusManager.clearFocus()
                }, text = {
                    Text(text = label)
                })
            }
        }
    }

}

@Preview(showBackground = false, widthDp = 400, heightDp = 300)
@Composable
fun FilterMenuPreview() {
    eMovieTheme {
        Surface() {
            FilterMenu(
                options = optionFakes,
                currentOption = optionFakes[0],
                label = "Years"
            ) {

            }
        }
    }
}