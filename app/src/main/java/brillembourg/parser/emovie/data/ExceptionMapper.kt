package brillembourg.parser.emovie.data

import brillembourg.parser.emovie.core.GenericException
import brillembourg.parser.emovie.core.NetworkException
import brillembourg.parser.emovie.core.ServerErrorException
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

fun Exception.toDomain(): Throwable {
    when (this) {
        is HttpException -> {
            return ServerErrorException(message())
        }
        is IOException -> {
            return NetworkException(message)
        }
        is CancellationException -> {
            //Cancellation must be propagated
            throw this
        }
        else -> {
            message?.let { return GenericException(it) }
            return GenericException(toString())
        }
    }
}