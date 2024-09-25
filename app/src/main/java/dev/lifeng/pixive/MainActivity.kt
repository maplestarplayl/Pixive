package dev.lifeng.pixive

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PixiveApplication.context = applicationContext
        //TODO weird workaround
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.light(1,10))
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        WorkManager.getInstance(this).cancelAllWork()
        //set homeFragment as default fragment
        //supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        //findNavController().navigate()

        //add refreshToken work to WorkManager
        //val refreshTokenWorkRequest = PeriodicWorkRequestBuilder<RefreshTokenWork>(10, java.util.concurrent.TimeUnit.MINUTES)
        //                                .setInitialDelay(10,java.util.concurrent.TimeUnit.MINUTES).build()
        //PixiveApplication.REFRESH_TOKEN_WORK_ID = refreshTokenWorkRequest.id
//        val refreshSpotlightsWorkRequest = PeriodicWorkRequestBuilder<PeriodGetSpotlightsWork>(1, java.util.concurrent.TimeUnit.DAYS)
//            .setInitialDelay(10, java.util.concurrent.TimeUnit.SECONDS).build()
//
//        //WorkManager.getInstance(this).enqueue(refreshTokenWorkRequest)
//        WorkManager.getInstance(this).enqueue(refreshSpotlightsWorkRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        //cancel all work when activity destroy(Actually, it's not how workManager should be used...)
        //WorkManager.getInstance(this).cancelWorkById(PixiveApplication.REFRESH_TOKEN_WORK_ID)
        //WorkManager.getInstance(this).cancelAllWork()
        Log.d("Work", "cancel refreshTokenWork")
    }
}