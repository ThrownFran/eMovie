package brillembourg.parser.emovie.presentation.utils

import brillembourg.parser.emovie.core.GenericException
import brillembourg.parser.emovie.core.NetworkException
import brillembourg.parser.emovie.core.ServerErrorException
import brillembourg.parser.emovie.presentation.models.UiText

fun getMessageFromException (e: Throwable): UiText {
    if(e !is Exception) {
       return UiText.UnexpectedError
    }

    return when (e) {
        is ServerErrorException -> UiText.UnexpectedError
        is NetworkException -> UiText.NoInternet
        is GenericException -> UiText.UnexpectedError
        else -> UiText.UnexpectedError
    }
}