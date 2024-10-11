package ua.wc.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun Date.currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}