package ua.wc.ads.appopen

import ua.wc.ads.AdError

expect class AppOpenAdManager() {
    fun load(unitId: String, callback: AppOpenAdLoadCallback)
    fun show(ad: AppOpenAd, callback: AppOpenAdShowCallback)
}

interface AppOpenAdLoadCallback {
    fun onAdLoaded(ad: AppOpenAd)
    fun onAdFailedToLoad(adError: AdError)
    fun onPaid(valueMicros: Long, currencyCode: String, source: String)
}

interface AppOpenAdShowCallback {
    fun onAdClicked()
    fun onAdFailedToShowFullScreenContent(adError: AdError)
    fun onAdDismissedFullScreenContent()
    fun onAdImpression()
    fun onAdShowedFullScreenContent()
}