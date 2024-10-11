package ua.wc.ads.native_banner

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import ua.wc.ads.AdError
import ua.wc.utils.PlatformContext

actual class NativeAdManager {
    actual fun load(unitId: String, callback: NativeAdCallback) {
        val context = PlatformContext.getActivityContext() ?: return
        val adLoader = AdLoader.Builder(context, unitId)
            .forNativeAd { ad: NativeAd ->
                ad.setOnPaidEventListener {
                    callback.onPaid(
                        cents = it.valueMicros / 1_000_000.0 * 100,
                        currencyCode = it.currencyCode,
                        source = ad.responseInfo?.mediationAdapterClassName ?: ""
                    )
                }
                callback.onAdLoaded(AndroidNativeAd(ad))
            }
            .withAdListener(object : AdListener() {
                override fun onAdClosed() {}

                override fun onAdLoaded() {}

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    callback.onAdFailedToLoad(AdError(loadAdError.code, loadAdError.message))
                }

                override fun onAdOpened() {}

                override fun onAdClicked() {
                    callback.onAdClicked()
                }

                override fun onAdImpression() {
                    onAdImpression()
                }
            })
            .build()

        val request = AdManagerAdRequest.Builder().build()
        adLoader.loadAd(request)
    }
}