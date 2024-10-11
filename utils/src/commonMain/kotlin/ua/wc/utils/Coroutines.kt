package ua.wc.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

inline fun CoroutineScope.ticker(
    delayMillis: Long,
    initialDelayMillis: Long = delayMillis,
    crossinline onTick: () -> Unit
): Job {
    return this.launch(Dispatchers.Default) {
        delay(initialDelayMillis)
        while (true) {
            withContext(Dispatchers.Main) { onTick() }
            delay(delayMillis)
        }
    }
}