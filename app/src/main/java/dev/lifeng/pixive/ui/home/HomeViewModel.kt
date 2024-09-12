package dev.lifeng.pixive.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import dev.lifeng.pixive.data.model.response.SpotArticle
import dev.lifeng.pixive.data.repo.dataStoreRepo
import dev.lifeng.pixive.data.repo.pagingRepo
import dev.lifeng.pixive.data.repo.repo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    var back: Boolean = false
    val recommendArtistsFlow = MutableStateFlow(PixivRecommendArtistsResponse(listOf(),""))

    fun updateRecommendArtists() {
        viewModelScope.launch{
            repo.getRecommendArtistsFlow()
                .catch { e ->
                    e.message?.let { Log.d("HomeViweModel", "catch error in HomeViewModel") }
                    //here using a hack technique to emit a empty list of artists and use nextUrl field to store the error message
                    emit(PixivRecommendArtistsResponse(listOf(), "networkError"))
                }.collect {
                    recommendArtistsFlow.value = it
                }
        }
    }

    val spotLightsFlow: StateFlow<PixivSpotlightResponse> =
         dataStoreRepo.getSpotlights().catch {
                e -> e.message?.let { Log.d("Network","catch error in viewModel") }
            emitAll(repo.getSpotlightsFlow())
        }.stateIn(viewModelScope, SharingStarted.Lazily, PixivSpotlightResponse(listOf(SpotArticle())))

    val getPixivIllustPagingData: Flow<PagingData<PixivRecommendIllusts.Illust>> =
            pagingRepo.getRecommendIllusts().cachedIn(viewModelScope)

}