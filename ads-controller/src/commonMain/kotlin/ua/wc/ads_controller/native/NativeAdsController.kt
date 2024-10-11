package ua.wc.ads_controller.native

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.wc.ads.AdError
import ua.wc.ads_controller.Ads
import ua.wc.ads.native_banner.NativeAd
import ua.wc.ads.native_banner.NativeAdCallback
import ua.wc.ads.native_banner.NativeAdManager
import ua.wc.ads_controller.AdsLogger
import ua.wc.utils.Date
import ua.wc.utils.currentTimeMillis
import ua.wc.utils.ticker
import kotlin.math.pow

class NativeAdsController(
    private val cacheSize: Int,
    private val logger: AdsLogger,
) {

    private val nativeAdManager = NativeAdManager()

    private val ads: MutableList<NativeAdContainer> = mutableListOf()
    private val attachedAds: MutableMap<String, NativeAdContainer> = mutableMapOf()
    private val listeners: MutableMap<String, NativeAdsListener> = mutableMapOf()
    private var isLoading: Boolean = false

    // Backoff
    private var retryTimes: Int = 0
    private val exponentialRate: Double = 2.0
    private val retryInterval = 2000L
    private var maxRetryDelay: Long = 60_000L
    private var delayDeadline: Long = 0
    private val isActiveBackoff: Boolean
        get() = Date.currentTimeMillis() < delayDeadline

    private var isActivated = false


    var isEnabled: Boolean = true
        set(value) {
            field = value
            listeners.forEach { entry -> entry.value.onEnabled(value) }
        }

    var analyticsListener: NativeAdAnalyticsListener? = null

    var adsTllMilliseconds = ADS_TTL

    fun activate(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            ticker(LOAD_ADS_DELAY, 0L) {
                if (Ads.isInitializedAds && isEnabled && isActiveBackoff.not()) {
                    refresh()
                }
            }
        }

        // Remove ads from cache if TTL was expired
        coroutineScope.launch {
            ticker(CHECK_ADS_TTL, CHECK_ADS_TTL) {
                if (Ads.isInitializedAds.not()) return@ticker
                validateAds()
            }
        }
    }

    fun load(
        key: String,
        onLoaded: (NativeAdContainer) -> Unit,
        onEnabled: (Boolean) -> Unit,
    ) {
        onEnabled(isEnabled)
        if (isEnabled.not()) return

        val ad = attachedAds[key]
        if (ad != null) {
            onLoaded(ad)
        } else {
            ads.removeFirstOrNull()?.let {
                attachedAds[key] = it
                onLoaded(it)
                // Force refresh
                refresh()
            }
        }

        listeners[key] = object : NativeAdsListener {
            override fun onLoaded(ad: NativeAdContainer) {
                onLoaded(ad)
            }

            override fun onEnabled(enabled: Boolean) {
                onEnabled(enabled)
            }
        }
    }

    private fun refresh() {
        if (isLoading || ads.size >= cacheSize) return
        logger(TAG, "NativeBanner: refresh")
        isLoading = true
        load { ad ->
            backoff(ad == null)
            ad?.let { ads.add(NativeAdContainer(it)) }
            attachAds()
            isLoading = false
        }
    }

    private fun backoff(activate: Boolean) {
        if (activate.not()) {
            retryTimes = 0
            delayDeadline = 0
            return
        }
        val delay = (retryInterval * exponentialRate.pow(retryTimes)).toLong()
            .coerceAtMost(maxRetryDelay)
        logger(TAG, "NativeBanner: use delay $delay")
        delayDeadline = Date.currentTimeMillis() + delay
        retryTimes++
    }

    // Attach cached ads to keys and notify listeners
    private fun attachAds() {
        listeners.forEach { (k, v) ->
            if (attachedAds[k] == null) {
                val ad = ads.firstOrNull() ?: return@forEach
                attachedAds[k] = ad
                v.onLoaded(ad)
                // Force refresh
                refresh()
            }
        }
    }

    // Remove expired ads
    private fun validateAds() {
        ads.removeAll { it.loadedAt + adsTllMilliseconds < Date.currentTimeMillis() }
        val unitIds = attachedAds
            .filter { entry -> entry.value.loadedAt + adsTllMilliseconds < Date.currentTimeMillis() }
            .onEach { entry -> entry.value.ad.destroy() }
            .map { entry -> entry.key }
        logger(TAG, "NativeBanner: expired ${unitIds.size}")
        unitIds.forEach {
            attachedAds.remove(it)
        }
    }

    private fun load(onComplete: (NativeAd?) -> Unit) {
        nativeAdManager.load(Ads.NativeUnitId, object : NativeAdCallback {
            override fun onAdLoaded(ad: NativeAd) {
                logger(TAG,"NativeBanner: loaded")
                analyticsListener?.onLoaded("", "")
                onComplete(ad)
            }

            override fun onAdFailedToLoad(adError: AdError) {
                logger(TAG,"NativeBanner: error ${adError.code} ${adError.message}")
                analyticsListener?.onFailedToLoad(adError.code, adError.message, "")
                onComplete(null)
            }

            override fun onPaid(cents: Double, currencyCode: String, source: String) {
                logger(TAG,"NativeBanner: paid")
                analyticsListener?.onPaid(
                    id = "",
                    value = cents,
                    currency = currencyCode,
                    source = source,
                )
            }

            override fun onAdClicked() {
                logger(TAG,"NativeBanner: clicked")
                analyticsListener?.onClicked()
            }

            override fun onAdImpression() {
                logger(TAG,"NativeBanner: impression")
                analyticsListener?.onImpression("", "")
            }

        })
        analyticsListener?.onRequest()
        logger(TAG,"NativeBanner: request")
    }

    companion object {
        private const val LOAD_ADS_DELAY = 1_000L

        private const val TAG = "NativeAdsController"

        private const val CHECK_ADS_TTL = 60_000L
        private const val ADS_TTL = 3600_000L
    }

}

interface NativeAdAnalyticsListener {
    fun onClicked() {}

    fun onFailedToLoad(code: Int, message: String, source: String) {}

    fun onImpression(id: String, source: String) {}

    fun onLoaded(id: String, source: String) {}

    fun onRequest() {}

    fun onPaid(id: String, value: Double, currency: String, source: String) {}
}

interface NativeAdsListener {
    fun onLoaded(ad: NativeAdContainer)
    fun onEnabled(enabled: Boolean)
}

class NativeAdContainer(
    val ad: NativeAd,
    val loadedAt: Long = Date.currentTimeMillis(),
)