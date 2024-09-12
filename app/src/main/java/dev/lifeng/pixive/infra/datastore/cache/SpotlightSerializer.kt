package dev.lifeng.pixive.infra.datastore.cache

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

object SpotlightSerializer : Serializer<PixivSpotlightResponse> {
    override val defaultValue: PixivSpotlightResponse = PixivSpotlightResponse(listOf())

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): PixivSpotlightResponse {
        try {
            val Jsonme = Json{
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase}
            return Jsonme.decodeFromStream(input)
        } catch (exception: InvalidProtocolBufferException) {
            Log.d("Serialization", "read Spotlight Failed : ${exception.message}")
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(t: PixivSpotlightResponse, output: OutputStream) {
        try{
            val Jsonme = Json{
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase}
            Jsonme.encodeToStream(t, output)
        }  catch (e: Exception){
            e.printStackTrace()
            Log.d("Serialization", "write Spotlight Failed : ${e.message}")
        }
    }
}

val Context.SpotLightDataStore: DataStore<PixivSpotlightResponse> by dataStore(
    fileName = "spotlight2.json",
    serializer = SpotlightSerializer
)
