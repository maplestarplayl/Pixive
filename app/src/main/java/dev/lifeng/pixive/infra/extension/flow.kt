package dev.lifeng.pixive.infra.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dev.lifeng.pixive.PixiveApplication
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// 用感知生命周期的方式收集流
fun <T> Flow<T>.collectIn(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
): Job = lifecycleOwner.lifecycleScope.launch {
    while (PixiveApplication.TOKEN == "") {
        //等待token获取
        delay(1000)
    }
    flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(action)
}