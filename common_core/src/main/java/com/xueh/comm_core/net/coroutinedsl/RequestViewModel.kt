package com.xueh.comm_core.net.coroutinedsl

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.mvvm.AbsViewModel
import com.xueh.comm_core.helper.launchSafety
import com.xueh.comm_core.net.BaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

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
                ViewModelDsl<Response>().apply(apiDSL).start?.invoke() ?: onApiStart()
            }
            onError { error ->
                ViewModelDsl<Response>().apply(apiDSL).error?.invoke(error) ?: onApiError(error)
            }
            onFinally {
                ViewModelDsl<Response>().apply(apiDSL).finally?.invoke()?: onApiFinally()
            }
        }

    protected open fun onApiStart() {
        apiLoading.value = true
        apiLoadingState.value = true
    }

    protected open fun onApiError(e: Exception) {
        onApiFinally()

        apiException.value = e
        apiExceptionState.value = e
    }

    protected open fun onApiFinally() {
        apiLoading.value = false
        apiLoadingState.value = false
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
                start?.invoke() ?: onApiStart()
            }.collect {
                if (it.isSuccess()) {
                    result.invoke(it.data)
                } else {
                    error?.invoke(Exception(it.errorMsg)) ?: onApiError(Exception(it.errorMsg))
                    Log.e("HTTP", "apiFlow---> Error---> errorCode=${it.errorCode} errorMsg=${it.errorMsg}")
                }
            }
        }.onCatch {
            error?.invoke(Exception(it.message)) ?: onApiError(Exception(it.message))
            Log.e("HTTP", "apiFlow---> Error---> ${it.message}")
        }.onComplete {
            finally?.invoke() ?: onApiFinally()
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //DSL包装 解析了BaseResult
    protected fun <Response> apiDslResult(
        apiDSL: ViewModelDsl<Response>.() -> Unit,
    ) {
        apiDSL<BaseResult<Response>> {
            onStart {
                ViewModelDsl<Response>().apply(apiDSL).start?.invoke()?:onApiStart()
            }
            onRequest {
                ViewModelDsl<Response>().apply(apiDSL).requestBaseResult()
            }
            onResponse {
                if (it.isSuccess()) {
                    ViewModelDsl<Response>().apply(apiDSL).response?.invoke(it.data)
                } else {
                    ViewModelDsl<Response>().apply(apiDSL).error?.invoke(Exception(it.errorMsg))?:onApiError(Exception(it.errorMsg))
                    Log.e("HTTP", "apiDslResult---> Error---> errorCode=${it.errorCode} errorMsg=${it.errorMsg}")
                }
            }
            onFinally {
                ViewModelDsl<Response>().apply(apiDSL).finally?.invoke()?:onApiFinally()
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}


