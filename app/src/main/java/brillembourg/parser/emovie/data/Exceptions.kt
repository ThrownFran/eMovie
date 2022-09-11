package brillembourg.parser.emovie.data

import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class NetworkException(message: String? = "") : Exception("Network error: $message")
class GenericException(message: String? = "") : Exception("Generic error: $message")
class ServerErrorException(message: String? = "") : Exception("Server error: $message")

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
            printStackTrace()
            val message = message
            message?.let {
                return GenericException(it)
            }
            return GenericException(toString())
        }
    }
}