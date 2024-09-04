package dev.lifeng.pixive.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.GET
import retrofit2.http.Header

interface PixivApi {
    @GET("/v1/spotlight/articles?filter=for_android&category=all")
    suspend fun getSpotlights(@Header("Authorization") token: String = PixiveApplication.TOKEN): PixivSpotlightResponse
    @GET("/v1/user/recommended?filter=for_android")
    suspend fun getRecommendArtists(@Header("Authorization") token: String = PixiveApplication.TOKEN): PixivRecommendArtistsResponse
    @GET("/v1/illust/recommended?filter=for_ios&include_ranking_label=true")
    suspend fun getRecommendIllusts(@Header("Authorization") token: String = PixiveApplication.TOKEN): PixivRecommendIllusts
    companion object{
        private const val BASE_URL = "https://app-api.pixiv.net"

        @OptIn(ExperimentalSerializationApi::class)
        fun create(): PixivApi {
            val JsonMe = Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            }
            return retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JsonMe.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(PixivApi::class.java)
        }
    }
}