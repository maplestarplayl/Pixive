package dev.lifeng.pixive.data.repo

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.data.network.PixivApi
import kotlinx.coroutines.flow.Flow

object pagingRepo{

    private val pixivApi = PixivApi.create()
    fun getRecommendIllusts(): Flow<PagingData<PixivRecommendIllusts.Illust>> {
        return Pager(
            config = PagingConfig(pageSize = 90),
            pagingSourceFactory = { PixivPagingSource(pixivApi) }
        ).flow
    }
}


class PixivPagingSource(private val pixivApi: PixivApi) : PagingSource<Int, PixivRecommendIllusts.Illust>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PixivRecommendIllusts.Illust> {
        return try {
            val page = params.key ?: 1
            val response = pixivApi.getRecommendIllusts()
            Log.d("RepoPagingSource", "load: $response ")
            val items = response.illusts
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (items.isNotEmpty()) page + 1 else null
            LoadResult.Page(items, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, PixivRecommendIllusts.Illust>): Int? = null

}