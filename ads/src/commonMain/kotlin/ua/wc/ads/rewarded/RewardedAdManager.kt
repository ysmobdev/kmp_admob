package ua.wc.ads.rewarded

import ua.wc.ads.AdError

expect class RewardedAdManager() {
    fun load(unitId: String, callback: RewardedAdLoadCallback)
    fun show(rewardedAd: RewardedAd, callback: RewardedAdShowCallback)
}

interface RewardedAdLoadCallback {
    fun onAdLoaded(ad: RewardedAd)
    fun onAdFailedToLoad(adError: AdError)
    fun onPaid(valueMicros: Long, currencyCode: String, source: String)
}

interface RewardedAdShowCallback {
    fun onAdClicked()
    fun onAdDismissedFullScreenContent()
    fun onAdFailedToShowFullScreenContent(adError: AdError)
    fun onAdImpression()
    fun onAdShowedFullScreenContent()
    fun onUserEarnedReward()
}