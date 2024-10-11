package ua.wc.kmpadmob.android

import android.app.Application
import ua.wc.utils.PlatformContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PlatformContext.setUpAppContext(this)
    }

}