package brillembourg.parser.emovie.presentation.utils

import brillembourg.parser.emovie.data.GenericException
import brillembourg.parser.emovie.data.NetworkException
import brillembourg.parser.emovie.data.ServerErrorException

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