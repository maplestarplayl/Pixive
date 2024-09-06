package dev.lifeng.pixive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.data.repo.pagingRepo
import kotlinx.coroutines.flow.Flow

class HomeViewModel: ViewModel() {

    fun getPixivIllustPagingData(): Flow<PagingData<PixivRecommendIllusts.Illust>> {
        return pagingRepo.getRecommendIllusts().cachedIn(viewModelScope)
    }
}