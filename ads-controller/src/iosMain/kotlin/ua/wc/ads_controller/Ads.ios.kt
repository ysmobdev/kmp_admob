package ua.wc.ads_controller

import cocoapods.Google_Mobile_Ads_SDK.GADMobileAds
import kotlinx.cinterop.ExperimentalForeignApi

actual object Ads {

    actual val RewardedUnitId: String = "ca-app-pub-3940256099942544/5224354917"
    actual val InterstitialUnitId: String = "ca-app-pub-3940256099942544/1033173712"
    actual val AppOpenUnitId: String = "ca-app-pub-3940256099942544/9257395921"
    actual val NativeUnitId: String = "ca-app-pub-3940256099942544/3986624511"
    actual var isInitializedAds: Boolean = false

    @OptIn(ExperimentalForeignApi::class)
    actual fun initialize(onComplete: () -> Unit) {
        if (isInitializedAds) return
        GADMobileAds.sharedInstance().startWithCompletionHandler {
            isInitializedAds = true
            onComplete()
        }
    }
}