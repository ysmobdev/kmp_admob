package ua.wc.ads_controller.native

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAdView
import ua.wc.ads.native_banner.AndroidNativeAd
import ua.wc.ads_controller.R

@Composable
actual fun PlatformAdsComposable(
    container: NativeAdContainer,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            LayoutInflater.from(context).inflate(R.layout.admob_banner, null)
        },
        update = {
            it.populateNativeAdView(container)
        },
        modifier = modifier
    )
}


private const val NATIVE_BANNER_REQUEST_LAYOUT_DELAY = 500L

private fun View.populateNativeAdView(container: NativeAdContainer?) {
    if (container == null) {
        visibility = View.GONE
        return
    }
    val nativeAd = (container.ad as AndroidNativeAd).ad
    visibility = View.VISIBLE

    val nativeAdTitle = findViewById<TextView>(R.id.native_ad_title)
    val nativeIconView = findViewById<ImageView>(R.id.native_icon_view)
    val nativeAdCallToAction = findViewById<Button>(R.id.native_ad_call_to_action)
    val nativeAdSocialContext = findViewById<TextView>(R.id.native_ad_social_context)

    nativeAdTitle.text = nativeAd.headline

    val nativeAdContainer = findViewById<NativeAdView>(R.id.native_ad_container)
    nativeAdContainer.headlineView = nativeAdTitle
    nativeAdContainer.bodyView = nativeAdSocialContext
    nativeAdContainer.iconView = nativeIconView
    nativeAdContainer.callToActionView = nativeAdCallToAction

    if (nativeAd.body == null) {
        nativeAdSocialContext.visibility = View.INVISIBLE
    } else {
        nativeAdSocialContext.visibility = View.VISIBLE
        nativeAdSocialContext.text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
        nativeAdCallToAction.visibility = View.INVISIBLE
    } else {
        nativeAdCallToAction.visibility = View.VISIBLE
        nativeAdCallToAction.text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
        nativeIconView.visibility = View.GONE
    } else {
        nativeAd.icon?.drawable?.let { nativeIconView.setImageDrawable(it) }
        nativeIconView.visibility = View.VISIBLE
    }

//    if (BuildConfig.DEBUG) {
//        findViewById<TextView>(R.id.ad_label).text =
//            "${simpleDateFormat.format(container.loadedAt)} |" +
//                    " ${container.ad.responseInfo?.responseId}"
//    }

    nativeAdContainer.setNativeAd(nativeAd)

    if (tag != true) {
        requestLayoutWithDelay(NATIVE_BANNER_REQUEST_LAYOUT_DELAY)
        tag = true
    }
}

private fun View.requestLayoutWithDelay(delayMillis: Long) {
    postDelayed({
        val t = parent?.parent?.parent
        if (t == null) {
            postDelayed({
                val k = parent?.parent?.parent
                if (k != null) {
                    k.requestLayout()
                } else {
//                    Timber.d("NativeBanner: parent is null again")
                }
            }, delayMillis)
        } else {
            t.requestLayout()
        }
    }, delayMillis)
}