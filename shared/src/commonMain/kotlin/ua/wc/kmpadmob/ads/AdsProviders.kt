package com.fxroyale.app.presentation.ads

import androidx.compose.runtime.staticCompositionLocalOf
import ua.wc.ads_controller.native.NativeAdsController

val LocalNativeAdsController = staticCompositionLocalOf<NativeAdsController> {
    error("Hasn't been provided")
}