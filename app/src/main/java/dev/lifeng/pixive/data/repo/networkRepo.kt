package dev.lifeng.pixive.data.repo

import android.util.Log
import dev.lifeng.pixive.data.network.PixivApi
import dev.lifeng.pixive.data.network.PixivAuthApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch

object repo{
    private const val PAGE_SIZE = 10
    private val pixivApi = PixivApi.create()
    private val pixivAuthApi = PixivAuthApi.create()
    //elegant way to handle network request
    suspend fun auth(): Result<String> = tryAndCatch { pixivAuthApi.auth().accessToken }
    suspend fun addBookMark(id: Int) = tryAndCatch { pixivApi.addBookMark(id = id) }
    suspend fun deleteBookMark(id: Int) = tryAndCatch { pixivApi.deleteBookMark(id = id) }
    fun getSpotlightsFlow() = tryAndCatchReturnFlow { pixivApi.getSpotlights() }
    fun getRecommendArtistsFlow() = tryAndCatchReturnFlow { pixivApi.getRecommendArtists() }
}


suspend fun <T> tryAndCatch(block:suspend () -> T): Result<T>{
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure<T>(e).onFailure {
            e.printStackTrace()
            Log.d("Network", "Failed to get data due to exception: ${e.message}")
        }
    }
}

fun <T>tryAndCatchReturnFlow(block:suspend () -> T): Flow<T> {
    return suspend { block() }
        .asFlow()
        .catch {
            e -> e.printStackTrace()
            Log.d("Network", "Failed to get FLow ata due to exception: ${e.message}")
        }.also { Log.d("Network", "tryAndCatchReturnFlow: $it") }
}