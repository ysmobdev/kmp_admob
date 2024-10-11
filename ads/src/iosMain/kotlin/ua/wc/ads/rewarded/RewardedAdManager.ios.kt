package ua.wc.ads.rewarded

actual class RewardedAdManager {
    actual fun load(unitId: String, callback: RewardedAdLoadCallback) {

    }

    actual fun show(
        rewardedAd: RewardedAd,
        callback: RewardedAdShowCallback
    ) {
    }
}