package dev.lifeng.pixive.infra.extension

import android.util.Log

class ChannelClosedException(msg: String) : Exception(msg) {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

suspend fun tryAndCatchChannelClosed(block: suspend () -> Unit,onErrorAction: () -> Unit ={}) {
    try {
        block()
    } catch (e: ChannelClosedException) {
        Log.d("Channel", "Channel is closed: ${e.message}")
    }catch (e: Exception) {
        e.printStackTrace()
        Log.d("Channel", "Failed due to exception: ${e.message}")
    }
}