package dev.lifeng.pixive.infra.extension

import android.util.Log
import coil.request.ErrorResult
import coil.request.SuccessResult
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer

sealed class ImageDownloadState {
    object Loading : ImageDownloadState()
    data class Progress(val percentage: Int) : ImageDownloadState()
    data class Success(val result: SuccessResult) : ImageDownloadState()
    data class Error(val result: ErrorResult) : ImageDownloadState()
}


class ProgressResponseBody(private val responseBody: okhttp3.ResponseBody,
                           private val progressCallback: (Int) -> Unit) : okhttp3.ResponseBody() {
    override fun contentLength(): Long  = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        return object : ForwardingSource(responseBody.source()) {
            var totalBytesRead = 0L

            override fun read(sink: okio.Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0

                // 计算并发送下载进度
                if (contentLength() != -1L) {
                    val progress = (totalBytesRead * 100 / contentLength()).toInt()
                    runBlocking {
                        Log.d("DownloadProgress", "progress: $progress")
                        progressCallback(progress)
                    }
                }
                return bytesRead
            }
        }.buffer()
    }
}
