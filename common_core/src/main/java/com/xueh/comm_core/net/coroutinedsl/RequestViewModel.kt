package com.xueh.comm_core.net.coroutinedsl

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 创 建 人: xueh
 * 创建日期: 2020/4/21 11:48
 * 备注：
 */
open class RequestViewModel : AbsViewModel() {
    private fun <Response> dslApi(apiDSL: ViewModelDsl<Response>.() -> Unit) =
        ViewModelDsl<Response>().apply {
            onRequest {
                ViewModelDsl<Response>().apply(apiDSL).request()
            }
            onResponse {
                ViewModelDsl<Response>().apply(apiDSL).onResponse?.invoke(it)
            }

            onResponseSuspend {
                ViewModelDsl<Response>().apply(apiDSL).onResponseSuspend?.invoke(it)
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

    protected fun <Response> apiDSL(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        dslApi(apiDSL).launch(this)
    }

    protected fun <Response> apiFlowDSL(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        dslApi(apiDSL).launchFlow(this)
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

    protected fun <Response> apiFlow(
        request: suspend () -> Response,
        block:  (Response) -> Unit
    ) {
        viewModelScope.launch {
            flow {
                emit(request())
            }.flowOn(Dispatchers.IO).onStart {
                onApiStart()
            }.onCompletion {
                onApiFinally()
            }.catch {
                onApiError(Exception(it.message))
            }.collect {
                block.invoke(it)
            }
        }
    }

    protected fun <Response> apiLiveData(
        context: CoroutineContext = EmptyCoroutineContext,
        timeoutInMs: Long = 3000L,
        request: suspend () -> Response
    ): LiveData<LiveDataResult<Response>> {
        return androidx.lifecycle.liveData(context, timeoutInMs) {
            onApiStart()
            emit(LiveDataResult.Start())
            try {
                emit(withContext(Dispatchers.IO) {
                    LiveDataResult.Response(request())
                })
            } catch (e: Exception) {
                e.printStackTrace()
                onApiError(e)
                emit(LiveDataResult.Error<Response>(e))
            } finally {
                onApiFinally()
                emit(LiveDataResult.Finally())
            }
        }
    }
}


/**
 * LiveDataResult 必须加泛型 不然response的泛型就会被擦除!!
 * damn it
 */
sealed class LiveDataResult<T> {
    class Start<T> : LiveDataResult<T>()
    class Finally<T> : LiveDataResult<T>()
    data class Response<T>(val response: T) : LiveDataResult<T>()
    data class Error<T>(val exception: Exception) : LiveDataResult<T>()
}
