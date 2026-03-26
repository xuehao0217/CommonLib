package com.xueh.comm_core.base.mvvm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xueh.comm_core.helper.launchSafety
import kotlinx.coroutines.*

/**
 * ViewModel 基类：提供 **按 key 互斥** 的协程启动与 LiveData/State 形式的加载、异常通道。
 *
 * [launchCancelLast]：**同一 key** 再次调用会先 `cancel` 上一次 Job，再启动新协程，适合搜索、切换 Tab 等场景。
 */
abstract class AbsViewModel : ViewModel() {
    open val apiException: MutableLiveData<Throwable> = MutableLiveData()
    open val apiLoading: MutableLiveData<Boolean> = MutableLiveData()

    open var apiLoadingState = mutableStateOf(false)
    open var apiExceptionState = mutableStateOf<Throwable?>(null)

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected var jobs = hashMapOf<String, Job>()

    /**
     * 使用 [key] 标识协程；新任务启动前取消同 key 的旧 [Job]，并在 [viewModelScope] 内 [launchSafety] 执行 [block]。
     */
    fun launchCancelLast(key: String, block: suspend CoroutineScope.() -> Unit) = run {
        jobs[key]?.cancel()
        viewModelScope.launchSafety {
            block.invoke(this)
        }.apply { jobs[key] = this }
    }
}