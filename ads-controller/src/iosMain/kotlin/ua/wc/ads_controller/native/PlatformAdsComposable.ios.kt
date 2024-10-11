package ua.wc.ads_controller.native

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.Google_Mobile_Ads_SDK.GADNativeAdView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.UIKit.NSLayoutAttributeHeight
import platform.UIKit.NSLayoutAttributeWidth
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.NSLayoutRelationEqual
import platform.UIKit.UIButton
import platform.UIKit.UIImageView
import platform.UIKit.UILabel
import platform.UIKit.loadNibNamed
import ua.wc.ads.native_banner.IosNativeAd
import ua.wc.utils.ticker

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun PlatformAdsComposable(
    container: NativeAdContainer,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            val nibView = NSBundle.mainBundle.loadNibNamed("AdBannerTemplate", null, null)!!.first()
            nibView as GADNativeAdView
        },
        modifier = modifier,
        update = { nativeAdView ->
            nativeAdView.populateNativeAdView(container)
        },
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative,
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun GADNativeAdView.populateNativeAdView(container: NativeAdContainer?) {
    val nativeAdView = this
    val nativeAd = (container?.ad as? IosNativeAd)?.ad

    (nativeAdView.headlineView as? UILabel)?.text = nativeAd?.headline
    nativeAdView.mediaView?.mediaContent = nativeAd?.mediaContent

    if (nativeAdView.mediaView != null && nativeAd != null && nativeAd.mediaContent.aspectRatio > 0) {
        val heightConstraint = NSLayoutConstraint.constraintWithItem(
            view1 = mediaView!!,
            attribute = NSLayoutAttributeHeight,
            relatedBy = NSLayoutRelationEqual,
            toItem = mediaView,
            _attribute = NSLayoutAttributeWidth,
            multiplier = 1 / nativeAd.mediaContent.aspectRatio,
            constant = 0.0
        )
        heightConstraint.active = true
    }

    (nativeAdView.bodyView as? UILabel)?.text = nativeAd?.body
    nativeAdView.bodyView?.hidden = nativeAd?.body == null

    (nativeAdView.callToActionView as? UIButton)?.setTitle(nativeAd?.callToAction, platform.UIKit.UIControlStateNormal)
    nativeAdView.callToActionView?.hidden = nativeAd?.callToAction == null

    (nativeAdView.iconView as? UIImageView)?.image = nativeAd?.icon?.image
    nativeAdView.iconView?.hidden = nativeAd?.icon == null

    (nativeAdView.advertiserView as? UILabel)?.text = nativeAd?.advertiser
    nativeAdView.advertiserView?.hidden = nativeAd?.advertiser == null

    nativeAdView.callToActionView!!.userInteractionEnabled = false

    nativeAdView.nativeAd = nativeAd
}