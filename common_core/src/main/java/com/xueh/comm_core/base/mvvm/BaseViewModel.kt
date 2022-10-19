package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.helper.launchSafety
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.coroutinedsl.RequestViewModel
import com.xueh.comm_core.net.coroutinedsl.ViewModelDsl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart


/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 11:56
 * 备注：
 */
abstract class BaseViewModel<E> : RequestViewModel() {
    protected val api by lazy {
        initApi()
    }

    protected abstract fun initApi(): E

    protected fun showLoading() {
        apiLoading.postValue(true)
    }

    protected fun hideLoading() {
        apiLoading.postValue(false)
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    launchNet( request = {
//        api.bannerList3()
//    },start = {
//        apiLoading.value=true
//    }, finally = {
//        apiLoading.value=false
//    }){
//        stateFlowDada.emit(it)
//    }
    fun <T> launchNet(
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


    protected fun <Response> apiFlowBaseResult(
        request: suspend () -> BaseResult<Response>,
        block: suspend (Response) -> Unit,
    ) {
        apiFlow({ request.invoke() }) {
            if (it.isSuccess()) {
                block.invoke(it.data)
            } else {
                Log.e("HTTP", "BaseViewModel--> ${it.errorMsg}")
            }
        }
    }

    //DSL
    protected fun <Response> apiDslBaseResult(
        apiDSL: ApiDslBaseResult<Response>.() -> Unit,
    ) {
        apiDSL<BaseResult<Response>> {
            onRequest {
                ApiDslBaseResult<Response>().apply(apiDSL).onRequest()
            }
            onResponse {
                if (it.isSuccess()) {
                    ApiDslBaseResult<Response>().apply(apiDSL)?.onResponse?.invoke(it.data)
                } else {
                    Log.e("HTTP", "BaseViewModel--> ${it.errorMsg}")
                }
            }
        }
    }


    class ApiDslBaseResult<Response> {
        lateinit var onRequest: suspend () -> BaseResult<Response>
        infix fun onRequest(request: suspend () -> BaseResult<Response>) {
            this.onRequest = request
        }

        internal var onResponse: ((Response) -> Unit)? = null
        infix fun onResponse(onResponse: ((Response) -> Unit)?) {
            this.onResponse = onResponse
        }
    }
}