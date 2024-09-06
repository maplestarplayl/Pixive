package dev.lifeng.pixive.data.repo

import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import dev.lifeng.pixive.infra.datastore.SpotLightDataStore
import kotlinx.coroutines.flow.Flow

object dataStoreRepo{

    fun getSpotlights(): Flow<PixivSpotlightResponse> {
        return PixiveApplication.context.SpotLightDataStore.data
            //TODO() 诡异的Bug 加了之后就寄了
//            .transform {
//            Log.d("Network", "check dataStoreRepo ${it.articles.size}")
//            check(it.articles.isNotEmpty())
        }
    }