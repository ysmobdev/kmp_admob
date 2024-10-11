package ua.wc.ads_controller.rewarded

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.wc.ads.AdError
import ua.wc.ads_controller.Ads
import ua.wc.ads.rewarded.RewardedAd
import ua.wc.ads.rewarded.RewardedAdLoadCallback
import ua.wc.ads.rewarded.RewardedAdManager
import ua.wc.ads.rewarded.RewardedAdShowCallback
import ua.wc.utils.Date
import ua.wc.utils.currentTimeMillis

class RewardedAdController(
    private val onLog: (String) -> Unit
) {

    private val ads: ArrayDeque<WrappedRewardedAd> = ArrayDeque()
    private var isLoading: Boolean = false
    private val rewardedAdManager = RewardedAdManager()

    var initCoolDownSeconds = 0

    var isEnabled: Boolean = true

    var isConfirmRequired: Boolean = true
    var onConfirmAdRequired = MutableStateFlow<ConfirmAdHolder?>(null)

    var analyticsListener: RewardedAnalyticsListener? = null

    var unlockAt = 0L
        set(value) {
            field = value
            val duration = value - Date.currentTimeMillis() / 1000
            if (duration > 0) {
                _coolDownSeconds.value = duration.toInt()
            }
        }

    var lockListener: ((Long) -> Unit)? = null

    private val _coolDownSeconds = MutableStateFlow(0)
    val coolDownSeconds = _coolDownSeconds.asStateFlow()

    private val _state = MutableStateFlow(State.Loading)
    val state = _state.asStateFlow()

    suspend fun activate() {
        coroutineScope {
            launch(Dispatchers.Default) {
                while (true) {
                    val current = _coolDownSeconds.value
                    if (current > 0) {
                        if (_state.value == State.Pending) {
                            _state.value = State.CoolDown
                        }
                        _coolDownSeconds.value = current - 1
                    } else if (_state.value == State.CoolDown) {
                        _state.value = State.Pending
                    }
                    delay(1_000)
                }
            }
        }

        coroutineScope {
            launch {
                while (true) {
                    refresh()
                    delay(DELAY)
                }
            }
        }

        coroutineScope {
            launch {
                while (true) {
                    validateAds()
                    delay(CHECK_ADS_TTL)
                }
            }
        }
    }

    private fun validateAds() {
        val hasExpired = ads.removeAll { it.loadedAt + ADS_TTL < Date.currentTimeMillis() }
        onLog("expired $hasExpired")
        if (ads.isEmpty()) _state.value = State.Loading
    }

    private fun refresh() {
        if (isLoading || ads.size == ADS_COUNT) return
        onLog("Count: ${ads.size}/$ADS_COUNT")
        isLoading = true
        if (ads.isEmpty()) _state.value = State.Loading

        analyticsListener?.onRequest()
        onLog("RewardedAd: load")
        rewardedAdManager.load(
            Ads.RewardedUnitId,
            object : RewardedAdLoadCallback {

                override fun onPaid(valueMicros: Long, currencyCode: String, source: String) {
                    onLog("RewardedAd: paid -> ${valueMicros / 1_000_000.0}")
                    analyticsListener?.onPaid(
                        value = valueMicros / 1_000_000.0,
                        currency = currencyCode,
                        source = source
                    )
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    onLog("RewardedAd: loaded")
                    isLoading = false
                    analyticsListener?.onLoaded()
                }

                override fun onAdFailedToLoad(adError: AdError) {
                    analyticsListener?.onFailedLoad(adError.code, adError.message)
                    isLoading = false
                    onLog("RewardedAd: failed to load -> (${adError.code}) ${adError.message}")
                }
            })
    }

    private fun lock() {
        _coolDownSeconds.tryEmit(initCoolDownSeconds)
        lockListener?.invoke(Date.currentTimeMillis() / 1000 + initCoolDownSeconds)
    }

    fun show(
        adDomain: AdDomain,
        withLock: Boolean = false,
        canBeDisabled: Boolean = false,
        extras: Map<String, Any> = emptyMap(),
        confirmed: Boolean = false,
        onEarned: (Boolean?) -> Unit,
    ) {
        if (canBeDisabled && isEnabled.not()) {
            onLog("rADC --> Disabled")
            onEarned(null)
            return
        }
        if (withLock && _coolDownSeconds.value > 0) {
            onLog("rADC --> Locked")
            return
        }
        if (isConfirmRequired && confirmed.not()) {
            if (onConfirmAdRequired.tryEmit(ConfirmAdHolder(adDomain) {
                    show(
                        adDomain,
                        withLock,
                        canBeDisabled,
                        extras,
                        true,
                        onEarned
                    )
                })) {
                return
            }
        }
        val ad = ads.removeFirstOrNull()
        if (ad == null) {
            onEarned(null)
            onLog("rADC --> Null")
            return
        }
        onLog("rADC --> Show")
        var earnedReward = false

        rewardedAdManager.show(ad.rewardedAd, object : RewardedAdShowCallback {
            override fun onAdClicked() {
                onLog("RewardedAd: clicked")
                analyticsListener?.onClicked(adDomain.toString())
            }

            override fun onAdDismissedFullScreenContent() {
                onLog("rADC --> Earned: $earnedReward")
                if (withLock) lock()
                if (earnedReward) {
                    analyticsListener?.onEarnedReward(adDomain.toString())
                }
                onEarned(earnedReward)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                onLog("RewardedAd: failed to show -> (${adError.code}) ${adError.message}")
                analyticsListener?.onFailed(
                    adDomain.toString(),
                    adError.code,
                    adError.message
                )
            }

            override fun onAdImpression() {
                onLog("RewardedAd: impression")
                analyticsListener?.onImpression(
                    adDomain.toString(),
                    extras
                )
            }

            override fun onAdShowedFullScreenContent() {
                onLog("RewardedAd: shown")
                analyticsListener?.onShowedFullScreenContent()
            }

            override fun onUserEarnedReward() {
                earnedReward = true
            }

        })
    }

    enum class State {
        Pending,
        Loading,
        CoolDown,
        Error
    }

    companion object {
        private const val ADS_COUNT = 1
        private const val DELAY = 5_000L
        private const val CHECK_ADS_TTL = 60_000L
        private const val ADS_TTL = 3600_000L
    }

}

interface RewardedAnalyticsListener {
    fun onLoaded()

    fun onFailedLoad(errorCode: Int, errorMessage: String)

    fun onFailed(type: String, errorCode: Int, errorMessage: String)

    fun onRequest()

    fun onShowedFullScreenContent()

    fun onEarnedReward(type: String)

    fun onPaid(
        value: Double,
        currency: String,
        source: String,
    )

    fun onImpression(type: String, extras: Map<String, Any>)

    fun onClicked(type: String)
}

class WrappedRewardedAd(
    val loadedAt: Long,
    val rewardedAd: RewardedAd,
)

class AdDomain

class ConfirmAdHolder(val adDomain: AdDomain, val onConfirmed: () -> Unit)