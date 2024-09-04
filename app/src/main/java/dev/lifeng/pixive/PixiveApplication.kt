package dev.lifeng.pixive

import android.app.Application
import android.content.Context

class PixiveApplication: Application() {
    @Suppress("StaticFieldLeak")
    companion object{
        lateinit var context: Context
        const val TOKEN = "Bearer iv7Qi-rRNlz2S16XXaik57kARDTFLq8lQaP4I0R8fkY"
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
