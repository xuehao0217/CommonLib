package com.xueh.comm_core.net.coroutinedsl

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.helper.launchSafety
import com.xueh.comm_core.net.BaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

// ------------------- DSL 核心类 -------------------
class ViewModelDsl<Response> {

    internal var request: (suspend () -> Response)? = null
    internal var requestBaseResult: (suspend () -> BaseResult<Response>)? = null

    internal var start: (() -> Unit)? = null
    internal var response: ((Response) -> Unit)? = null
    //使用flow 需要Suspend 在协程 中操作  MutableStateFlow<List<BannerVO>>(emptyList())
    internal var responseSuspend: (suspend (Response) -> Unit)? = null
    internal var error: ((Exception) -> Unit)? = null
    internal var finally: (() -> Unit)? = null

    // 链式调用
    infix fun onStart(block: (() -> Unit)?) = apply { start = block }
    infix fun onRequest(block: suspend () -> Response) = apply { request = block }
    infix fun onRequestBaseResult(block: suspend () -> BaseResult<Response>) = apply { requestBaseResult = block }
    infix fun onResponse(block: ((Response) -> Unit)?) = apply { response = block }
    //使用flow 需要Suspend 在协程 中操作  MutableStateFlow<List<BannerVO>>(emptyList())
    infix fun onResponseSuspend(block: (suspend (Response) -> Unit)?) = apply { responseSuspend = block }
    infix fun onError(block: ((Exception) -> Unit)?) = apply { error = block }
    infix fun onFinally(block: (() -> Unit)?) = apply { finally = block }

    // ------------------- 普通请求 -------------------
    fun launch(viewModel: RequestViewModel) {
        val req = requireNotNull(request) { "❌  request 没有设置，请在 onRequest { ... } 中提供请求逻辑" }
        viewModel.viewModelScope.launchSafety {
            start?.invoke() ?: viewModel.apiLoading(true)
            runCatching { withContext(Dispatchers.IO) { req() } }.onSuccess {
                    response?.invoke(it)
                    responseSuspend?.invoke(it)
                }.onFailure {
                    error?.invoke(Exception(it)) ?: viewModel.apiError(it)
                }
            finally?.invoke() ?: viewModel.apiFinally()
        }
    }

    // ------------------- BaseResult 请求 -------------------
    fun launchBaseResult(viewModel: RequestViewModel) {
        val req = requireNotNull(requestBaseResult) { "❌  requestBaseResult 没有设置，请在 onRequestBaseResult { ... } 中提供请求逻辑" }
        viewModel.viewModelScope.launchSafety {
            start?.invoke() ?: viewModel.apiLoading(true)
            runCatching { withContext(Dispatchers.IO) { req() } }.onSuccess { result ->
                    if (result.isSuccess()) {
                        response?.invoke(result.data)
                        responseSuspend?.invoke(result.data)
                    } else {
                        val ex = Exception(result.errorMsg)
                        error?.invoke(ex) ?: viewModel.apiError(ex)
                        Log.e("HTTP", "ErrorCode=${result.errorCode} Msg=${result.errorMsg}")
                    }
                }.onFailure { error?.invoke(Exception(it)) ?: viewModel.apiError(it) }
            finally?.invoke() ?: viewModel.apiFinally()
        }
    }

    // ------------------- Flow 请求 -------------------
    fun launchFlow(viewModel: RequestViewModel) {
        val req = requireNotNull(request) { "❌  request 没有设置，请在 onRequest { ... } 中提供请求逻辑" }
        viewModel.viewModelScope.launchSafety {
            flow { emit(req()) }.flowOn(Dispatchers.IO)
                .onStart { start?.invoke() ?: viewModel.apiLoading(true) }
                .catch { error?.invoke(Exception(it)) ?: viewModel.apiError(it) }
                .onCompletion { finally?.invoke() ?: viewModel.apiFinally() }
                .collect { response?.invoke(it); responseSuspend?.invoke(it) }
        }
    }

}