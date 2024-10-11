package ua.wc.ads.appopen

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import ua.wc.ads.AdError
import ua.wc.utils.PlatformContext

actual class AppOpenAdManager {
    actual fun load(unitId: String, callback: AppOpenAdLoadCallback) {
        val context = PlatformContext.getAppContext() ?: throw IllegalStateException("Context is required")
        val request = AdRequest.Builder().build()
        com.google.android.gms.ads.appopen.AppOpenAd.load(context, unitId, request, object :
            com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                callback.onAdFailedToLoad(AdError(p0.code, p0.message))
            }

            override fun onAdLoaded(ad: com.google.android.gms.ads.appopen.AppOpenAd) {
                ad.onPaidEventListener = OnPaidEventListener { p0 ->
                    callback.onPaid(
                        valueMicros = p0.valueMicros,
                        currencyCode = p0.currencyCode,
                        source = ad.responseInfo.mediationAdapterClassName ?: ""
                    )
                }
                callback.onAdLoaded(AppOpenAd(ad))

            }
        })
    }
    actual fun show(
        ad: AppOpenAd,
        callback: AppOpenAdShowCallback
    ) {
//        val activity = ua.wc.ads_controller.Ads.activity?.get() ?: throw IllegalStateException("Activity is required")
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                callback.onAdClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                callback.onAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                callback.onAdFailedToShowFullScreenContent(AdError(p0.code, p0.message))
            }

            override fun onAdImpression() {
                callback.onAdImpression()
            }

            override fun onAdShowedFullScreenContent() {
                callback.onAdShowedFullScreenContent()
            }
        }
//        ad.show(activity)
    }
}