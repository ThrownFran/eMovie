package brillembourg.parser.emovie.data

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class NetworkException(message: String? = ""): Exception("Network error: $message")
class GenericException(message: String? = ""): Exception("Generic error: $message")

fun Exception.toDomain(): Throwable {
    when (this) {
        is IOException -> {
            return NetworkException(message)
        }
        is CancellationException -> {
            throw this
        }
        else -> {
            printStackTrace()
            val message = message
            message?.let {
                return GenericException(it)
            }
            return GenericException(toString())
        }
    }
}