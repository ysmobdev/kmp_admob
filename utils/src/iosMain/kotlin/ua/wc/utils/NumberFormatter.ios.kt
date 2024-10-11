package ua.wc.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.CoreFoundation.CFNumberFormatterCreateStringWithNumber
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.numberWithLong
import platform.darwin.SInt64

actual object NumberFormatter {
        
    private val formatter = NSNumberFormatter()
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun short(value: Long, precision: Int): String {
        formatter.maximumFractionDigits = precision.convert()
        val number = NSNumber.numberWithLong(value)
        return formatter.stringFromNumber(number)!!
    }
}