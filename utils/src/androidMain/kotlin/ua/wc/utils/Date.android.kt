package ua.wc.utils

actual fun Date.currentTimeMillis(): Long {
    return System.currentTimeMillis()
}