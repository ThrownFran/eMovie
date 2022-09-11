package brillembourg.parser.emovie.core

class NetworkException(message: String? = "") : Exception("Network error: $message")

class ServerErrorException(message: String? = "") : Exception("Server error: $message") {
    init { Logger.error(this) }
}

class GenericException(message: String? = "") : Exception("Generic error: $message") {
    init { Logger.error(this) }
}

