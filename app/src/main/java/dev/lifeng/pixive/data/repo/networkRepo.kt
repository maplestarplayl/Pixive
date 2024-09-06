package dev.lifeng.pixive.data.repo

import android.util.Log
import dev.lifeng.pixive.data.network.PixivApi
import dev.lifeng.pixive.data.network.PixivAuthApi

object repo{
    private const val PAGE_SIZE = 10
    private val pixivApi = PixivApi.create()
    private val pixivAuthApi = PixivAuthApi.create()
    //elegant way to handle network request
    suspend fun auth(): Result<String> = tryAndCatch { pixivAuthApi.auth().accessToken }
    suspend fun getSpotlights() = tryAndCatch { pixivApi.getSpotlights() }
    suspend fun getRecommendArtists() = tryAndCatch { pixivApi.getRecommendArtists() }
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