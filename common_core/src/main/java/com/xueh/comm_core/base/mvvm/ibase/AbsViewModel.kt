package com.xueh.comm_core.base.mvvm.ibase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.net.BaseResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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




    fun <T> launchNet(
        showLoading: Boolean = false,
        request: suspend () -> BaseResult<T>,
        start: (() -> Unit)? = null,
        error: ((Throwable) -> Unit)? = null,
        finally: (() -> Unit)? = null,
        result: suspend (T) -> Unit,
    ) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("网络异常，请检查网络设置")
            error?.invoke(Exception("网络异常，请检查网络设置"))
            return
        }
        launch(showLoading=showLoading,onError = {
            error?.invoke(Exception(it.message))
            ToastUtils.showShort(it.message.toString())
        }) {
            flow {
                emit(request())
            }.flowOn(Dispatchers.IO).onStart {
                start?.invoke()
            }.onCompletion {
                finally?.invoke()
            }.collect {
                if (it.isSuccess()) {
                    result.invoke(it.data)
                } else {
                    error?.invoke(Exception(it.errorMsg))
                    ToastUtils.showShort(it.errorMsg)
                }
            }
        }
    }
}