package dev.lifeng.pixive.infra.datastore.preference

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import dev.lifeng.pixive.data.model.savedData.UserSetting
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

val Context.UserSettingsDataStore: DataStore<UserSetting> by dataStore(
    fileName = "user_settings.json",
    serializer = UserSettingsSerializer
)

object UserSettingsSerializer: Serializer<UserSetting>{
    override val defaultValue: UserSetting = UserSetting("", 0)

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: java.io.InputStream): UserSetting {
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
    override suspend fun writeTo(t: UserSetting, output: java.io.OutputStream) {
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
