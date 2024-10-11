package ua.wc.ads_controller

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import ua.wc.utils.PlatformContext
import java.util.Arrays

actual object Ads {
    actual val RewardedUnitId: String = "ca-app-pub-3940256099942544/5224354917"
    actual val InterstitialUnitId: String = "ca-app-pub-3940256099942544/1033173712"
    actual val AppOpenUnitId: String = "ca-app-pub-3940256099942544/9257395921"
    actual val NativeUnitId: String = "ca-app-pub-3940256099942544/2247696110"

    actual var isInitializedAds: Boolean = false

    actual fun initialize(onComplete: () -> Unit) {
        if (isInitializedAds) return
        val context = PlatformContext.getAppContext() ?: return
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("2E6CC559B8025440AE3FCF3C2365FBEF"))
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(context) {
            println(it)
            isInitializedAds = true
            onComplete()
        }
    }

}