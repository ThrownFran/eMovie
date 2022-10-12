package brillembourg.parser.emovie.core

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MovieApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        installJavaTimeBackport()
    }

    private fun installJavaTimeBackport() {
        AndroidThreeTen.init(this)
    }
}