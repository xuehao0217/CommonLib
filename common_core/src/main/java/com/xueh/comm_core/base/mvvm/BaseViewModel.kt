package com.xueh.comm_core.base.mvvm

import android.util.Log
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.coroutinedsl.RequestViewModel
import com.xueh.comm_core.net.coroutinedsl.ViewModelDsl


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

}