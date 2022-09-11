package brillembourg.parser.emovie.presentation.utils

import android.content.Context
import androidx.annotation.IdRes
import brillembourg.parser.emovie.R


sealed class UiText() {
    data class DynamicString(val value: String) : UiText()
    object NoInternet : UiText()
    object UnexpectedError : UiText()
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.DynamicString -> this.value
        is UiText.NoInternet -> context.getString(R.string.no_internet)
        is UiText.UnexpectedError -> context.getString(R.string.unexpected_error)
    }
}


