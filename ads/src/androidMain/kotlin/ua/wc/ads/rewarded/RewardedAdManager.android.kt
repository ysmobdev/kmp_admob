package ua.wc.ads.rewarded

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import ua.wc.utils.PlatformContext

actual class RewardedAdManager {
    actual fun load(unitId: String, callback: RewardedAdLoadCallback) {
        val context = PlatformContext.getAppContext() ?: throw IllegalStateException("Context is required")
        val adRequest = AdManagerAdRequest.Builder().build()
        com.google.android.gms.ads.rewarded.RewardedAd.load(
            context,
            unitId,
            adRequest,
            object : com.google.android.gms.ads.rewarded.RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    callback.onAdFailedToLoad(ua.wc.ads.AdError(adError.code, adError.message))
                }

                override fun onAdLoaded(ad: com.google.android.gms.ads.rewarded.RewardedAd) {
                    callback.onAdLoaded(RewardedAd(ad))
                    ad.onPaidEventListener = OnPaidEventListener {
                        callback.onPaid(
                            valueMicros = it.valueMicros,
                            currencyCode = it.currencyCode,
                            source = ad.responseInfo.mediationAdapterClassName ?: ""
                        )
                    }
                }
            })
    }

    actual fun show(
        rewardedAd: RewardedAd,
        callback: RewardedAdShowCallback
    ) {
//        val activity = ua.wc.ads_controller.Ads.activity?.get() ?: throw IllegalStateException("Activity is required")
        rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                callback.onAdClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                callback.onAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                callback.onAdFailedToShowFullScreenContent(ua.wc.ads.AdError(p0.code, p0.message))
            }

            override fun onAdImpression() {
                callback.onAdImpression()
            }

            override fun onAdShowedFullScreenContent() {
                callback.onAdShowedFullScreenContent()
            }
        }
//        rewardedAd.show(activity) {
//            callback.onUserEarnedReward()
//        }
    }
}