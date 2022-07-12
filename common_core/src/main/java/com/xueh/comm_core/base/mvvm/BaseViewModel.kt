package com.xueh.comm_core.base.mvvm

import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.coroutinedsl.RequestViewModel


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
            block.invoke(it.data)
        }
    }
}