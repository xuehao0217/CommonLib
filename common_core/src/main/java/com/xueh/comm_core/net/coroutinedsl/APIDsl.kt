package com.xueh.comm_core.net.coroutinedsl

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.R
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.utils.CommonUtils.getString
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
            runCatching {
                withContext(Dispatchers.IO) {
                    request()
                }
            }.onSuccess {
                onResponse?.invoke(it)
                onResponseSuspend?.invoke(it)
            }.onFailure {
                onError?.invoke(Exception(it))
            }
            onFinally?.invoke()
        }
    }


    internal fun launchFlow(viewModelScope: AbsViewModel) {
        viewModelScope.viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            onError?.invoke(Exception(throwable.message))
        }){
            flow {
                emit(request())
            }.flowOn(Dispatchers.IO).onStart {
                onStart?.invoke()
            }.onCompletion {
                onFinally?.invoke()
            }.catch {
                onError?.invoke(Exception(it.message))
            }.collect {
                onResponse?.invoke(it)
                onResponseSuspend?.invoke(it)
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //使用flow 需要Suspend 在协程 中操作  MutableStateFlow<List<BannerVO>>(emptyList())
    internal var onResponseSuspend: (suspend (Response) -> Unit)? = null
    infix fun onResponseSuspend(onResponse: (suspend (Response) -> Unit)?) {
        this.onResponseSuspend = onResponse
    }
    /////////////////////////////////////////////////////////////////////////////////////

}