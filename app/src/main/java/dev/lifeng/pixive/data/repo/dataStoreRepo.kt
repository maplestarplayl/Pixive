package dev.lifeng.pixive.data.repo

import android.util.Log
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import dev.lifeng.pixive.infra.datastore.cache.SpotLightDataStore
import dev.lifeng.pixive.infra.datastore.preference.UserSettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

object dataStoreRepo {

    fun getSpotlights(): Flow<PixivSpotlightResponse> {
        return PixiveApplication.context.SpotLightDataStore.data
            .transform {
                Log.d("Network", "check dataStoreRepo ${it.articles.size}")
                check(it.articles.isNotEmpty())
                emit(it)
            }
    }

    fun getUserSettings() = PixiveApplication.context.UserSettingsDataStore.data
        .transform {
            Log.d("Network", "check dataStoreRepo $it")
            check(it.refreshToken != "")
            emit(it)
        }

}