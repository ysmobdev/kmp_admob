package ua.wc.utils

import java.text.DecimalFormat
import kotlin.math.abs

actual object NumberFormatter {
    
    private val df = DecimalFormat()
    
    actual fun short(value: Long, precision: Int): String {
        val (a, suffix) = when {
            abs(value) >= 1_000_000_000_000.0 -> value / 1_000_000_000_000.0 to "T"
            abs(value) >= 1_000_000_000.0 -> value / 1_000_000_000.0 to "B"
            abs(value) >= 1_000_000.0 -> value / 1_000_000.0 to "M"
            abs(value) >= 1_000.0 -> value / 1_000.0 to "К"
            else -> value to ""
        }
        df.maximumFractionDigits = precision
        return "${df.format(a)}$suffix"
    }

}