package brillembourg.parser.emovie.presentation.utils

object Logger {

    fun error(e: Exception) {
        e.printStackTrace()
        //Consider uploading to fabric
    }

    fun error(t: Throwable) {
        t.printStackTrace()
    }
}