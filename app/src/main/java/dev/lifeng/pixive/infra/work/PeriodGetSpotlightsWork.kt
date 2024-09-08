package dev.lifeng.pixive.infra.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.lifeng.pixive.data.repo.repo
import dev.lifeng.pixive.infra.datastore.cache.SpotLightDataStore
import kotlinx.coroutines.flow.catch

class PeriodGetSpotlightsWork(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params){
    override suspend fun doWork(): Result {
        try{
            repo.getSpotlightsFlow().catch { e ->
                Log.d("PeriodGetSpotlightsWork", "Failed to get spotlights due to network error")
                Log.d("PeriodGetSpotlightsWork", "the exception is ${e.message}")
                throw e
            }.collect {
                applicationContext.SpotLightDataStore.updateData { it }
                Log.d("PeriodGetSpotlightsWork", "Successfully get spotlights")
            }
        }catch (e: Exception){
            return Result.failure()
        }
        return Result.success()
    }
}