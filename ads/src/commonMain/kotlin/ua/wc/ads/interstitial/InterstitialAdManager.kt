package ua.wc.ads.interstitial

import ua.wc.ads.AdError

expect class InterstitialAdManager {
    fun load(unitId: String, callback: InterstitialAdLoadCallback)
    fun show(interstitialAd: InterstitialAd, callback: InterstitialAdShowCallback)
}

interface InterstitialAdLoadCallback {
    fun onAdLoaded(ad: InterstitialAd)
    fun onAdFailedToLoad(adError: AdError)
    fun onPaid(valueMicros: Long, currencyCode: String, source: String)
}

interface InterstitialAdShowCallback {
    fun onAdClicked()
    fun onAdFailedToShowFullScreenContent(adError: AdError)
    fun onAdDismissedFullScreenContent()
    fun onAdImpression()
    fun onAdShowedFullScreenContent()
}