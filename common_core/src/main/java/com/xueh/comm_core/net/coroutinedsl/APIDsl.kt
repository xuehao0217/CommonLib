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

    internal var start: (() -> Boolean?)? = null

    internal var response: ((Response) -> Unit)? = null

    internal var error: ((Exception) -> Boolean?)? = null

    internal var finally: (() -> Boolean?)? = null


    infix fun onStart(start: (() -> (Boolean?))?) {
        this.start = start
    }

    infix fun onRequest(request: suspend () -> Response) {
        this.request = request
    }

    infix fun onResponse(response: ((Response) -> Unit)?) {
        this.response = response
    }

    infix fun onError(error: ((Exception) -> Boolean?)?) {
        this.error = error
    }

    infix fun onFinally(finally: (() -> Boolean?)?) {
        this.finally = finally
    }


    internal fun launch(viewModelScope: AbsViewModel) {
        viewModelScope.viewModelScope.launch {
            start?.invoke()
            runCatching {
                withContext(Dispatchers.IO) {
                    request()
                }
            }.onSuccess {
                response?.invoke(it)
                responseSuspend?.invoke(it)
            }.onFailure {
                error?.invoke(Exception(it))
            }
            finally?.invoke()
        }
    }


    internal fun launchFlow(viewModelScope: AbsViewModel) {
        viewModelScope.viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            error?.invoke(Exception(throwable.message))
        }) {
            flow {
                emit(request())
            }.flowOn(Dispatchers.IO).onStart {
                start?.invoke()
            }.onCompletion {
                finally?.invoke()
            }.catch {
                error?.invoke(Exception(it.message))
            }.collect {
                response?.invoke(it)
                responseSuspend?.invoke(it)
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //使用flow 需要Suspend 在协程 中操作  MutableStateFlow<List<BannerVO>>(emptyList())
    internal var responseSuspend: (suspend (Response) -> Unit)? = null
    infix fun onResponseSuspend(responseSuspend: (suspend (Response) -> Unit)?) {
        this.responseSuspend = responseSuspend
    }

    //解析了BaseResult
    internal lateinit var requestParseData: suspend () -> BaseResult<Response>
    infix fun onRequestParseData(request: suspend () -> BaseResult<Response>) {
        this.requestParseData = request
    }
    /////////////////////////////////////////////////////////////////////////////////////

}