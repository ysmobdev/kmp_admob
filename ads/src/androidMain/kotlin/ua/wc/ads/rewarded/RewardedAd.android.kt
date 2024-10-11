package ua.wc.ads.rewarded

import android.app.Activity
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewarded.RewardedAd

actual class RewardedAd private constructor() {
    private lateinit var ad: RewardedAd

    constructor(ad: RewardedAd) : this() {
        this.ad = ad
    }

    var fullScreenContentCallback: FullScreenContentCallback?
        set(value) {
            ad.fullScreenContentCallback = value
        }
        get() = ad.fullScreenContentCallback

    fun show(activity: Activity, onUserEarnedReward: () -> Unit) {
        ad.show(activity) {
            onUserEarnedReward()
        }
    }
}