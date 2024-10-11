package ua.wc.ads_controller

fun interface AdsLogger {
    operator fun invoke(tag: String, log: String)
}