package ua.wc.ads.interstitial

import android.app.Activity
import com.google.android.gms.ads.FullScreenContentCallback

actual class InterstitialAd private constructor() {
    private lateinit var ad: com.google.android.gms.ads.interstitial.InterstitialAd

    constructor(ad: com.google.android.gms.ads.interstitial.InterstitialAd) : this() {
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