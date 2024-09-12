package dev.lifeng.pixive.infra.extension

import android.util.Log
import kotlinx.coroutines.withTimeout

suspend fun withTimeoutAndCatch(timeout: Long, block: suspend () -> Unit, onErrorAction: () -> Unit) {
    try {
        withTimeout(timeout) {
            block()
        }
    } catch (e: Exception) {
        Log.d("Coroutine", "Failed to get data due to exception: ${e.message}")
        onErrorAction()
    }
}
