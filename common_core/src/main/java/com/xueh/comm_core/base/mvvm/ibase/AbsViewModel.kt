package com.xueh.comm_core.base.mvvm.ibase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 15:24
 * 备注：
 */
//abstract class AbsViewModel :AndroidViewModel(Utils.getApp())
abstract class AbsViewModel : ViewModel() {
    open val apiException: MutableLiveData<Throwable> = MutableLiveData()
    open val apiLoading: MutableLiveData<Boolean> = MutableLiveData()


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected var jobs = hashMapOf<String, Job>()

    //开启协程 如果有上一个任务 则取消
    //key 协程的 key
    fun launchCancelLast(key: String, block: suspend CoroutineScope.() -> Unit) {
        jobs[key]?.let {
            it.cancel()
        }
        jobs.put(key, launch {
            block.invoke(this)
        })
    }

    //安全开启协程
    fun launch(
        showLoading: Boolean = true,
        onError: (suspend ((Throwable) -> Unit))? = null,
        block: suspend CoroutineScope.() -> Unit,
    ) = viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
        apiException.value = throwable
        LogUtils.eTag("BaseViewModel", "BaseViewModel CoroutineException --》${throwable}")
    }) {
        apiLoading.value = showLoading
        kotlin.runCatching {
            block.invoke(this)
        }.onFailure {
            //为了解决 suspend
            apiException.value = it
            onError?.invoke(it)
            LogUtils.eTag("BaseViewModel", "BaseViewModel try catch --》${it}")
        }
        apiLoading.value = false
    }
}