package ua.wc.ads.native_banner

import cocoapods.Google_Mobile_Ads_SDK.GADAdLoader
import cocoapods.Google_Mobile_Ads_SDK.GADAdLoaderAdTypeNative
import cocoapods.Google_Mobile_Ads_SDK.GADAdValue
import cocoapods.Google_Mobile_Ads_SDK.GADNativeAd
import cocoapods.Google_Mobile_Ads_SDK.GADNativeAdDelegateProtocol
import cocoapods.Google_Mobile_Ads_SDK.GADNativeAdLoaderDelegateProtocol
import cocoapods.Google_Mobile_Ads_SDK.GADPaidEventHandler
import cocoapods.Google_Mobile_Ads_SDK.GADRequest
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import ua.wc.ads.AdError
import ua.wc.utils.PlatformContext

actual class NativeAdManager actual constructor() {
    @OptIn(ExperimentalForeignApi::class)
    actual fun load(unitId: String, callback: NativeAdCallback) {
        val rootViewController = PlatformContext.rootUIViewController
        val adLoader =
            GADAdLoader(
                unitId,
                rootViewController, listOf(GADAdLoaderAdTypeNative), null
            )
        adLoader.delegate = object : NSObject(), GADNativeAdLoaderDelegateProtocol {

            override fun adLoader(adLoader: GADAdLoader, didFailToReceiveAdWithError: NSError) {
                callback.onAdFailedToLoad(
                    AdError(
                        didFailToReceiveAdWithError.code.toInt(),
                        didFailToReceiveAdWithError.localizedDescription
                    )
                )
            }

            override fun adLoader(adLoader: GADAdLoader, didReceiveNativeAd: GADNativeAd) {
                didReceiveNativeAd.paidEventHandler = object : GADPaidEventHandler {
                    override fun invoke(value: GADAdValue?) {
                        callback.onPaid(
                            cents = value!!.value.doubleValue * 100,
                            currencyCode = value.currencyCode,
                            source = ""
                        )
                    }

                }
                didReceiveNativeAd.delegate = object : NSObject(), GADNativeAdDelegateProtocol {
                    override fun nativeAdDidRecordClick(nativeAd: GADNativeAd) {
                        callback.onAdClicked()
                    }

                    override fun nativeAdDidRecordSwipeGestureClick(nativeAd: GADNativeAd) {
                        callback.onAdClicked()
                    }

                    override fun nativeAdDidRecordImpression(nativeAd: GADNativeAd) {
                        callback.onAdImpression()
                    }
                }
                callback.onAdLoaded(IosNativeAd(didReceiveNativeAd))
            }
        }
        adLoader.loadRequest(GADRequest())
    }
}
