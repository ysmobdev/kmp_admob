package ua.wc.ads_controller

expect object Ads {
    val RewardedUnitId: String
    val InterstitialUnitId: String
    val AppOpenUnitId: String
    val NativeUnitId: String
    var isInitializedAds: Boolean
    fun initialize(onComplete: () -> Unit)
}