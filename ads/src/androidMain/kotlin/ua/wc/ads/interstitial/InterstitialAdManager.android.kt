package ua.wc.ads.interstitial

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import ua.wc.ads.AdError
import ua.wc.utils.PlatformContext

actual class InterstitialAdManager {
    actual fun load(
        unitId: String,
        callback: InterstitialAdLoadCallback
    ) {
        val context = PlatformContext.getAppContext() ?: throw IllegalStateException("Context is required")
        val adRequest = AdRequest.Builder().build()
        com.google.android.gms.ads.interstitial.InterstitialAd.load(
            context,
            unitId,
            adRequest,
            object : com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    callback.onAdFailedToLoad(AdError(adError.code, adError.message))
                }

                override fun onAdLoaded(ad: com.google.android.gms.ads.interstitial.InterstitialAd) {
                    callback.onAdLoaded(InterstitialAd(ad))
                    ad.onPaidEventListener = OnPaidEventListener {
                        callback.onPaid(
                            valueMicros = it.valueMicros,
                            currencyCode = it.currencyCode,
                            source = ad.responseInfo.loadedAdapterResponseInfo?.adSourceName ?: "-"
                        )
                    }
                }
            })
    }

    actual fun show(
        interstitialAd: InterstitialAd,
        callback: InterstitialAdShowCallback
    ) {
//        val activity = ua.wc.ads_controller.Ads.activity?.get() ?: throw IllegalStateException("Activity is required")
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
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
//        interstitialAd.show(activity)
    }
}