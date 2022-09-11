package brillembourg.parser.emovie.core

//Consider uploading to fabric
object Logger {

    fun error(e: Exception) {
        e.printStackTrace()
    }

    fun error(t: Throwable) {
        t.printStackTrace()
    }
}