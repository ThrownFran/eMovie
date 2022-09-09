package brillembourg.parser.emovie.presentation.utils

object Logger {

    fun error(e: Exception) {
        e.printStackTrace()
        //Consider uploading to fabric
    }
}