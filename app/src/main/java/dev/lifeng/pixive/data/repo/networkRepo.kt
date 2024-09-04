package dev.lifeng.pixive.data.repo

import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import dev.lifeng.pixive.data.network.PixivApi

object repo{
    private const val PAGE_SIZE = 10
    private val pixivApi = PixivApi.create()

    suspend fun getSpotlights(): Result<PixivSpotlightResponse> {
        return try {
            Result.success(pixivApi.getSpotlights())
        }catch (e: Exception) {
            Result.failure<PixivSpotlightResponse>(e).onFailure { e.printStackTrace() }
        }
    }
    suspend fun getRecommendArtists(): Result<PixivRecommendArtistsResponse> {
        return try {
            Result.success(pixivApi.getRecommendArtists())
        }catch (e: Exception) {
            Result.failure<PixivRecommendArtistsResponse>(e).onFailure { e.printStackTrace() }
        }
    }
}