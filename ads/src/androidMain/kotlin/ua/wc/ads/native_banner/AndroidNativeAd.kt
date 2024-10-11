package ua.wc.ads.native_banner

import com.google.android.gms.ads.nativead.NativeAd

class AndroidNativeAd(
    val ad: NativeAd
) : ua.wc.ads.native_banner.NativeAd() {

    override fun destroy() {
        ad.destroy()
    }

}