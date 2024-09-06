package dev.lifeng.pixive

import android.app.Application
import android.content.Context
import java.util.UUID

class PixiveApplication: Application() {
    @Suppress("StaticFieldLeak")
    companion object{
        lateinit var context: Context
        var TOKEN: String = ""
        lateinit var REFRESH_TOKEN_WORK_ID: UUID
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
