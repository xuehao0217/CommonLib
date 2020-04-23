package com.xueh.comm_core.net.coroutine

import androidx.lifecycle.viewModelScope
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 创 建 人: xueh
 * 创建日期: 2020/4/21 11:48
 * 备注：
 */

class ViewModelDsl<Response> {

    internal lateinit var request: suspend () -> Response

    internal var onStart: (() -> Boolean?)? = null

    internal var onResponse: ((Response) -> Unit)? = null

    internal var onError: ((Exception) -> Boolean?)? = null

    internal var onFinally: (() -> Boolean?)? = null


    infix fun onStart(onStart: (() -> (Boolean?))?) {
        this.onStart = onStart
    }

    infix fun onRequest(request: suspend () -> Response) {
        this.request = request
    }

    infix fun onResponse(onResponse: ((Response) -> Unit)?) {
        this.onResponse = onResponse
    }

    infix fun onError(onError: ((Exception) -> Boolean?)?) {
        this.onError = onError
    }

    infix fun onFinally(onFinally: (() -> Boolean?)?) {
        this.onFinally = onFinally
    }


    internal fun launch(viewModelScope: AbsViewModel) {
        viewModelScope.viewModelScope.launch {
            onStart?.invoke()
            try {
                val response = withContext(Dispatchers.IO) {
                    request()
                }
                onResponse?.invoke(response)
            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke(e)
            } finally {
                onFinally?.invoke()
            }
        }
    }
}