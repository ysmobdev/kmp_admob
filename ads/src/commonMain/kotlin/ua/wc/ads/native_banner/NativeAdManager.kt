package ua.wc.ads.native_banner

import ua.wc.ads.AdError

expect class NativeAdManager() {
    fun load(unitId: String, callback: NativeAdCallback)
}

interface NativeAdCallback {
    fun onAdLoaded(ad: NativeAd)
    fun onAdFailedToLoad(adError: AdError)
    fun onPaid(cents: Double, currencyCode: String, source: String)
    fun onAdClicked()
    fun onAdImpression()
}
