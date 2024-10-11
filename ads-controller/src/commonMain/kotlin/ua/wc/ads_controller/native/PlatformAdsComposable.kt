package ua.wc.ads_controller.native

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformAdsComposable(container: NativeAdContainer, modifier: Modifier)