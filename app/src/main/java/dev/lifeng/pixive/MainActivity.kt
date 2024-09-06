package dev.lifeng.pixive

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.lifeng.pixive.infra.work.PeriodGetSpotlightsWork
import dev.lifeng.pixive.infra.work.RefreshTokenWork

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //add refreshToken work to WorkManager
        val refreshTokenWorkRequest = PeriodicWorkRequestBuilder<RefreshTokenWork>(10, java.util.concurrent.TimeUnit.MINUTES)
                                        .setInitialDelay(10,java.util.concurrent.TimeUnit.MINUTES).build()
        PixiveApplication.REFRESH_TOKEN_WORK_ID = refreshTokenWorkRequest.id
        val refreshSpotlightsWorkRequest = PeriodicWorkRequestBuilder<PeriodGetSpotlightsWork>(1, java.util.concurrent.TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueue(refreshTokenWorkRequest)
        WorkManager.getInstance(this).enqueue(refreshSpotlightsWorkRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        //cancel all work when activity destroy(Actually, it's not how workManager should be used...)
        WorkManager.getInstance(this).cancelWorkById(PixiveApplication.REFRESH_TOKEN_WORK_ID)
        Log.d("Work", "cancel refreshTokenWork")
    }
}