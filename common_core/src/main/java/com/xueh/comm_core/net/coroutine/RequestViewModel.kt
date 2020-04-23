package com.xueh.comm_core.net.coroutine

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

    @JvmOverloads
    protected fun <Response> apiCallback(
        request: suspend () -> Response,
        onResponse: ((Response) -> Unit),
        onStart: (() -> Boolean)? = null,
        onError: ((java.lang.Exception) -> Boolean)? = null,
        onFinally: (() -> Boolean)? = null
    ) {

        api<Response> {
            onRequest {
                request.invoke()
            }
            onResponse {
                onResponse.invoke(it)
            }
            onStart {
                val override = onStart?.invoke()
                if (override == null || !override) {
                    onApiStart()
                }
                false
            }
            onError {
                val override = onError?.invoke(it)
                if (override == null || !override) {
                    onApiError(it)
                }
                false
            }
            onFinally {
                val override = onFinally?.invoke()
                if (override == null || !override) {
                    onApiFinally()
                }
                false
            }
        }
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


    protected fun <Response> apiLiveData(
        context: CoroutineContext = EmptyCoroutineContext,
        timeoutInMs: Long = 3000L,
        request: suspend () -> Response
    ): LiveData<Result<Response>> {

        return androidx.lifecycle.liveData(context, timeoutInMs) {
            emit(Result.Start())
            try {
                emit(withContext(Dispatchers.IO) {
                    Result.Response(request())
                })
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(e))
            } finally {
                emit(Result.Finally())
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

}

/**
 * Result必须加泛型 不然response的泛型就会被擦除!!
 * damn it
 */
sealed class Result<T> {
    class Start<T> : Result<T>()
    class Finally<T> : Result<T>()
    data class Response<T>(val response: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
}
