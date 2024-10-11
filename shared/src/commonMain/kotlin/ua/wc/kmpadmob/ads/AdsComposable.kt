package com.fxroyale.app.presentation.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.wc.ads_controller.AdsKey
import ua.wc.ads_controller.native.NativeAdContainer
import ua.wc.ads_controller.native.NativeAdsController
import ua.wc.ads_controller.native.PlatformAdsComposable

private sealed class State() {
    object Loading : State()
    object Disabled : State()
    class Loaded(val container: NativeAdContainer) : State()
}

@Composable
fun AdsComposable(
    modifier: Modifier = Modifier,
    adsKey: AdsKey,
    controller: NativeAdsController = LocalNativeAdsController.current
) {
    var state by remember(adsKey.screen) { mutableStateOf<State>(State.Disabled) }
    LaunchedEffect(adsKey) {
        controller.load(
            key = adsKey.screen,
            onLoaded = { container ->
                state = State.Loaded(container = container)
            },
            onEnabled = {
                state = if (it) State.Loading else State.Disabled
            },
        )

    }

    when (state) {
        is State.Loaded -> {
            val container = (state as State.Loaded).container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF383B4E))
            ) {
                PlatformAdsComposable(
                    container = container,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        State.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF383B4E))
            )
        }

        else -> {}
    }
}