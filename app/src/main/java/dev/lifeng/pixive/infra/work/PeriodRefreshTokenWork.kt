package dev.lifeng.pixive.infra.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.data.network.PixivAuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RefreshTokenWork(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params){
    override suspend fun doWork(): Result {
        return try {
            // do something
            withContext(Dispatchers.IO){ PixiveApplication.TOKEN = "Bearer " +PixivAuthApi.create().auth().accessToken }
            Log.d("Auth", "GET Token from periodic work: ${PixiveApplication.TOKEN}")
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}