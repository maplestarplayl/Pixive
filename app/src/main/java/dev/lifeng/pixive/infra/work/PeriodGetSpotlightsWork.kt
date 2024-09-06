package dev.lifeng.pixive.infra.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.lifeng.pixive.data.repo.repo
import dev.lifeng.pixive.infra.datastore.SpotLightDataStore

class PeriodGetSpotlightsWork(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params){
    override suspend fun doWork(): Result {
        return try {
            val response = repo.getSpotlights()
            when(response.isSuccess){
                true -> {
                    applicationContext.SpotLightDataStore.updateData { response.getOrNull()!! }
                    Log.d("PeriodGetSpotlightsWork", "Successfully get spotlights")
                    Result.success()
                }
                false -> {
                    Log.d("PeriodGetSpotlightsWork", "Failed to get spotlights due to network error")
                    Log.d("PeriodGetSpotlightsWork", "the exception is ${response.exceptionOrNull().toString()}")
                    Result.failure()
                }
            }
        }catch (e: Exception){
            Log.d("PeriodGetSpotlightsWork", "Failed to get spotlights due to exception")
            Log.d("PeriodGetSpotlightsWork", "the exception is ${e.message}")
            return Result.failure()
        }
    }
}