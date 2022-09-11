package brillembourg.parser.emovie.core

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieApp : Application() {

    override fun onCreate() {
        super.onCreate()
        installJavaTimeBackport()
    }

    private fun installJavaTimeBackport() {
        AndroidThreeTen.init(this)
    }
}