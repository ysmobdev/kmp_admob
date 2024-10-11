package ua.wc.kmpadmob

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fxroyale.app.presentation.ads.AdsComposable
import com.fxroyale.app.presentation.ads.LocalNativeAdsController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import ua.wc.ads_controller.Ads
import ua.wc.ads_controller.AdsKey
import ua.wc.ads_controller.native.NativeAdsController

private val nativeAdsController =
    NativeAdsController(2) { tag, log -> Napier.d(message = log, tag = tag) }

private var isLoggerReady = false

private fun initLogger() {
    if (isLoggerReady) return
    isLoggerReady = true
    Napier.base(DebugAntilog())
}

@Composable
fun App() {
    val backgroundScope = CoroutineScope(Dispatchers.IO)
    LaunchedEffect(Unit) {
        initLogger()
        backgroundScope.launch {
            Ads.initialize {
                nativeAdsController.activate(backgroundScope)
            }
        }
    }
    CompositionLocalProvider(
        LocalNativeAdsController provides nativeAdsController
    ) {
        val navController = rememberNavController()
        Scaffold(backgroundColor = Color.White) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).statusBarsPadding()) {
                Box(Modifier.fillMaxWidth().height(30.dp).background(Color.LightGray))
                AdsComposable(modifier = Modifier, adsKey = AdsKey.First)
                Box(Modifier.fillMaxWidth().height(30.dp).background(Color.LightGray))
//                NavHost(
//                    modifier = Modifier.weight(1f).fillMaxWidth(),
//                    navController = navController,
//                    startDestination = Home,
//                    enterTransition = { EnterTransition.None },
//                    exitTransition = { ExitTransition.None },
//                ) {
//                    composable<Home> {
//                        Scaffold {
//                            Column(modifier = Modifier.fillMaxWidth()) {
//                                Text("First", color = Color.Black)
//                                Box(Modifier.fillMaxWidth().height(30.dp).background(Color.LightGray))
//                                AdsComposable(modifier = Modifier, adsKey = AdsKey.First)
//                                Box(Modifier.fillMaxWidth().height(30.dp).background(Color.LightGray))
//                                Button(onClick = { navController.navigate(Second) }) {
//                                    Text("Open next screen", color = Color.White)
//                                }
//                            }
//                        }
//                    }
//                    composable<Second> {
//                        Scaffold {
//                            Column(modifier = Modifier.fillMaxWidth()) {
//                                Text("Second", color = Color.Black)
//                                Box(Modifier.fillMaxWidth().height(30.dp).background(Color.LightGray))
//                                AdsComposable(modifier = Modifier, adsKey = AdsKey.Second)
//                                Box(Modifier.fillMaxWidth().height(30.dp).background(Color.LightGray))
//                                Button(onClick = { navController.navigateUp() }) {
//                                    Text("Back", color = Color.White)
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}

@kotlinx.serialization.Serializable
object Home

@kotlinx.serialization.Serializable
object Second
