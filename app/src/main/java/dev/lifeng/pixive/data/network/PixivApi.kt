package dev.lifeng.pixive.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.data.model.response.PixivAuthResponse
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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

interface PixivAuthApi {
    @POST("/auth/token")
    @FormUrlEncoded
    suspend fun auth(@Field("client_id") clientId: String = "MOBrBDS8blbauoSck0ZfDbtuzpyT",
                     @Field("client_secret")clientSecret: String = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj",
                     @Field("grant_type")type: String = "refresh_token",
                     @Field("refresh_token")token: String = "q3TjuYw_vTrF7SDvMT1bFs7pm44JOPSvWegLxyIsmhI",
                     @Field("include_policy")policy: Boolean = true): PixivAuthResponse
    companion object{
        private const val BASE_URL = "https://oauth.secure.pixiv.net"
        @OptIn(ExperimentalSerializationApi::class)
        fun create(): PixivAuthApi {
            val JsonMe = Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            }
            return retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JsonMe.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(PixivAuthApi::class.java)
        }
    }
}