package ua.wc.kmpadmob.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ua.wc.kmpadmob.App
import ua.wc.utils.PlatformContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformContext.setUpActivityContext(this)
        setContent {
            App()
        }
    }
}
