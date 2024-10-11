package ua.wc.utils

expect object NumberFormatter {
    
    fun short(value: Long, precision: Int = 1): String
    
}