package com.xueh.comm_core.net.coroutinedsl

import androidx.lifecycle.LiveData
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 创 建 人: xueh
 * 创建日期: 2020/4/21 11:48
 * 备注：
 */
open class RequestViewModel : AbsViewModel() {

    private fun <Response> api(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        ViewModelDsl<Response>().apply(apiDSL).launch(this)
    }

    protected fun <Response> apiDSL(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        api<Response> {
            onRequest {
                ViewModelDsl<Response>().apply(apiDSL).request()
            }
            onResponse {
                ViewModelDsl<Response>().apply(apiDSL).onResponse?.invoke(it)
            }
            onStart {
                val override = ViewModelDsl<Response>().apply(apiDSL).onStart?.invoke()
                if (override == null || !override) {
                    onApiStart()
                }
                override
            }
            onError { error ->
                val override = ViewModelDsl<Response>().apply(apiDSL).onError?.invoke(error)
                if (override == null || !override) {
                    onApiError(error)
                }
                override

            }
            onFinally {
                val override = ViewModelDsl<Response>().apply(apiDSL).onFinally?.invoke()
                if (override == null || !override) {
                    onApiFinally()
                }
                override
            }
        }
    }

    protected open fun onApiStart() {
        apiLoading.value = true
    }

    protected open fun onApiError(e: Exception?) {
        apiLoading.value = false
        apiException.value = e
    }

    protected open fun onApiFinally() {
        apiLoading.value = false
    }


    protected fun <Response> apiLiveData(
        context: CoroutineContext = EmptyCoroutineContext,
        timeoutInMs: Long = 3000L,
        request: suspend () -> Response
    ): LiveData<LiveResult<Response>> {

        return androidx.lifecycle.liveData(context, timeoutInMs) {
            onApiStart()
            emit(LiveResult.Start())
            try {
                emit(withContext(Dispatchers.IO) {
                    LiveResult.Response(request())
                })
            } catch (e: Exception) {
                e.printStackTrace()
                onApiError(e)
                emit(LiveResult.Error(e))
            } finally {
                onApiFinally()
                emit(LiveResult.Finally())
            }
        }
    }
}


/**
 * Result必须加泛型 不然response的泛型就会被擦除!!
 * damn it
 */
sealed class LiveResult<T> {
    class Start<T> : LiveResult<T>()
    class Finally<T> : LiveResult<T>()
    data class Response<T>(val response: T) : LiveResult<T>()
    data class Error<T>(val exception: Exception) : LiveResult<T>()
}
