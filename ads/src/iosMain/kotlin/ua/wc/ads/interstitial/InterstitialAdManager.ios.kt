package ua.wc.ads.interstitial

actual class InterstitialAdManager {
    actual fun load(
        unitId: String,
        callback: InterstitialAdLoadCallback
    ) {
    }

    actual fun show(
        interstitialAd: InterstitialAd,
        callback: InterstitialAdShowCallback
    ) {
    }
}