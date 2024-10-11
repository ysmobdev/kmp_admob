package ua.wc.utils

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
actual object PlatformContext {

    private var appContext: Context? = null
    private var activityContext: Context? = null

    fun setUpAppContext(context: Context) {
        this.appContext = context
    }

    fun getAppContext(): Context? {
        return appContext
    }

    fun setUpActivityContext(context: Context) {
        this.activityContext = context
    }

    fun getActivityContext(): Context? {
        return activityContext
    }


}