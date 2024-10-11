package ua.wc.ads.native_banner

import cocoapods.Google_Mobile_Ads_SDK.GADNativeAd
import kotlinx.cinterop.ExperimentalForeignApi

class IosNativeAd @OptIn(ExperimentalForeignApi::class) constructor(
    val ad: GADNativeAd
) : ua.wc.ads.native_banner.NativeAd() {

    override fun destroy() {

    }

}