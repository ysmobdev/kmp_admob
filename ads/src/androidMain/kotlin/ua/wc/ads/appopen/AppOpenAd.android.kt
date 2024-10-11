package ua.wc.ads.appopen

import android.app.Activity
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd

actual class AppOpenAd private constructor() {

    private lateinit var ad: AppOpenAd

    constructor(ad: AppOpenAd) : this() {
        this.ad = ad
    }

    var fullScreenContentCallback: FullScreenContentCallback?
        set(value) {
            ad.fullScreenContentCallback = value
        }
        get() = ad.fullScreenContentCallback

    fun show(activity: Activity) {
        ad.show(activity)
    }

}