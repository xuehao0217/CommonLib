package com.xueh.comm_core.net.coroutinedsl

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.launchSafety
import com.xueh.comm_core.net.BaseResult
import kotlinx.coroutines.CoroutineExceptionHandler
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
                ViewModelDsl<Response>().apply(apiDSL).response?.invoke(it)
            }

            onResponseSuspend {
                ViewModelDsl<Response>().apply(apiDSL).responseSuspend?.invoke(it)
            }

            onStart {
                val override = ViewModelDsl<Response>().apply(apiDSL).start?.invoke()
                if (override == null || !override) {
                    onApiStart()
                }
                override
            }
            onError { error ->
                val override = ViewModelDsl<Response>().apply(apiDSL).error?.invoke(error)
                if (override == null || !override) {
                    onApiError(error)
                }
                override

            }
            onFinally {
                val override = ViewModelDsl<Response>().apply(apiDSL).finally?.invoke()
                if (override == null || !override) {
                    onApiFinally()
                }
                override
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //DSL
    protected fun <Response> apiDSL(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        dslApi(apiDSL).launch(this)
    }

    protected fun <Response> apiFlowDSL(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        dslApi(apiDSL).launchFlow(this)
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    apiFlow( request = {
//        api.bannerList3()
//    },start = {
//        apiLoading.value=true
//    }, finally = {
//        apiLoading.value=false
//    }){
//        stateFlowDada.emit(it)
//    }
    fun <T> apiFlow(
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
        viewModelScope.launchSafety {
            flow {
                emit(request())
            }.flowOn(Dispatchers.IO).onStart {
                start?.invoke()
            }.collect {
                if (it.isSuccess()) {
                    result.invoke(it.data)
                } else {
                    error?.invoke(Exception(it.errorMsg))
                    ToastUtils.showShort(it.errorMsg)
                }
            }
        }.onCatch {
            error?.invoke(Exception(it.message))
            ToastUtils.showShort(it.message.toString())
        }.onComplete {
            finally?.invoke()
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //DSL包装 解析了BaseResult
    protected fun <Response> apiDslResult(
        apiDSL: ViewModelDsl<Response>.() -> Unit,
    ) {
        apiDSL<BaseResult<Response>> {
            onRequest {
                ViewModelDsl<Response>().apply(apiDSL).requestParseData()
            }
            onResponse {
                if (it.isSuccess()) {
                    ViewModelDsl<Response>().apply(apiDSL)?.response?.invoke(it.data)
                } else {
                    ViewModelDsl<Response>().apply(apiDSL)?.error?.invoke(Exception(it.errorMsg))
                    ToastUtils.showShort("${it.errorMsg}")
                    Log.e("HTTP", "apiDslResult--> ${it.errorMsg}")
                }
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //包装了LiveData
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

    //    viewModel.loadLiveData().observe(this@HomeFragment) {
    //        when (it) {
    //            is RequestViewModel.LiveDataResult.Response -> {
    //                ToastUtils.showShort(it.response.data.toString())
    //            }
    //            is RequestViewModel.LiveDataResult.Error -> {
    //
    //            }
    //            is RequestViewModel.LiveDataResult.Start -> {
    //
    //            }
    //            is RequestViewModel.LiveDataResult.Finally -> {
    //
    //            }
    //        }
    //    }
    protected fun <Response> apiLiveData(
        context: CoroutineContext = EmptyCoroutineContext,
        timeoutInMs: Long = 3000L,
        request: suspend () -> Response,
    ) = androidx.lifecycle.liveData(context, timeoutInMs) {
        onApiStart()
        emit(LiveDataResult.Start())
        kotlin.runCatching {
            emit(withContext(Dispatchers.IO) {
                LiveDataResult.Response(request())
            })
        }.onFailure {
            it.printStackTrace()
            onApiError(Exception(it))
            emit(LiveDataResult.Error<Response>(Exception(it)))
        }
        onApiFinally()
        emit(LiveDataResult.Finally())
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}


