package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.coroutinedsl.RequestViewModel
import com.xueh.comm_core.net.coroutinedsl.ViewModelDsl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


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


    protected fun <Response> apiFlowBaseResult(
        request: suspend () -> BaseResult<Response>,
        block: suspend (Response) -> Unit
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
        apiDSL: ApiDslBaseResult<Response>.() -> Unit
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


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected var jobs = hashMapOf<String, Job>()
    //开启协程 如果有上一个任务 则取消
    //key 协程的 key
    fun launchCancelLast(key:String, block: suspend CoroutineScope.() -> Unit){
        jobs.get(key)?.let {
            it.cancel()
        }
        jobs.put(key, launch{
            block.invoke(this)
        })
    }

    //安全开启协程
    fun launch(
        showLoading:Boolean=true,
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