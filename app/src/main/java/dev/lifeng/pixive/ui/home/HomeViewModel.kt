package dev.lifeng.pixive.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import dev.lifeng.pixive.data.repo.dataStoreRepo
import dev.lifeng.pixive.data.repo.pagingRepo
import dev.lifeng.pixive.data.repo.repo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onEach

class HomeViewModel: ViewModel() {


    suspend fun spotLightsFlow(): Flow<PixivSpotlightResponse>{
        return dataStoreRepo.getSpotlights().onEach { Log.d("Network","data passed inside getSpotlights $it") }.catch {
            e -> e.message?.let { Log.d("Network","catch error in viewModel") }
            emitAll(repo.getSpotlightsFlow())
        }
        //return repo.getSpotlightsFlow()
    }

    suspend fun recommendArtistsFlow(): Flow<PixivRecommendArtistsResponse>{
        return repo.getRecommendArtistsFlow().onEach { Log.d("Netwrok","data passed inside getSpotlights") }.catch {
            e -> e.message?.let { Log.d("HomeViweModel","catch error in viewModel") }
            //here using a hack techique to emit a empty list of artists and use nextUrl field to store the error message
            emit(PixivRecommendArtistsResponse(listOf(),e.message?:""))
        }
    }

    fun getPixivIllustPagingData(): Flow<PagingData<PixivRecommendIllusts.Illust>> {
        return pagingRepo.getRecommendIllusts().onEach { Log.d("Netwrok","data passed inside getSpotlights") }.cachedIn(viewModelScope)
    }
}