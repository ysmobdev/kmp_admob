package ua.wc.ads_controller

sealed class AdsKey(val screen: String) {

    data object Second : AdsKey("Second")

    data object First : AdsKey("First")

}