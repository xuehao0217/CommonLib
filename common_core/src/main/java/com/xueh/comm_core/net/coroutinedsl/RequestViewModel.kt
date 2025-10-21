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

open class RequestViewModel : AbsViewModel() {

    // ------------------- DSL 调用 -------------------

    protected fun <T> apiDSL(block: ViewModelDsl<T>.() -> Unit) =
        runIfNetworkAvailable { ViewModelDsl<T>().apply(block).launch(this) }

    protected fun <T> apiFlowDSL(block: ViewModelDsl<T>.() -> Unit) =
        runIfNetworkAvailable { ViewModelDsl<T>().apply(block).launchFlow(this) }

    protected fun <T> apiDslResult(block: ViewModelDsl<T>.() -> Unit) =
        runIfNetworkAvailable { ViewModelDsl<T>().apply(block).launchBaseResult(this) }

    // ------------------- Flow 简化请求 -------------------

    protected fun <T> apiFlow(
        request: suspend () -> BaseResult<T>,
        start: (() -> Unit)? = null,
        error: ((Throwable) -> Unit)? = null,
        finally: (() -> Unit)? = null,
        result: suspend (T) -> Unit,
    ) = runIfNetworkAvailable {
        viewModelScope.launchSafety {
            flow { emit(request()) }
                .flowOn(Dispatchers.IO)
                .onStart { start?.invoke() ?: apiLoading(true) }
                .catch { throwable ->
                    val ex = Exception(throwable)
                    error?.invoke(ex) ?: apiError(ex)
                }
                .onCompletion { finally?.invoke() ?: apiFinally() }
                .collect { baseResult ->
                    if (baseResult.isSuccess()) {
                        result(baseResult.data)
                    } else {
                        val ex = Exception(baseResult.errorMsg)
                        error?.invoke(ex) ?: apiError(ex)
                        Log.e(
                            "HTTP",
                            "apiFlow ErrorCode=${baseResult.errorCode} Msg=${baseResult.errorMsg}"
                        )
                    }
                }
        }
    }

    // ------------------- 通用状态逻辑 -------------------

    open fun apiLoading(value: Boolean) {
        apiLoading.value = value
        apiLoadingState.value = value
    }

    open fun apiError(
        e: Throwable,
        showToast: Boolean = true,
        log: Boolean = true
    ) {
        val ex = e as? Exception ?: Exception(e)
        apiException.value = ex
        apiExceptionState.value = ex

        if (log) Log.e("API_ERROR", ex.message ?: "未知错误", ex)
        if (showToast) ToastUtils.showShort(ex.message ?: "网络异常")

        apiFinally()
    }

    open fun apiFinally() {
        apiLoading.value = false
        apiLoadingState.value = false
    }

    // ------------------- 工具方法 -------------------

    /** 统一网络检查入口，避免多处重复逻辑 */
    private inline fun runIfNetworkAvailable(block: () -> Unit) {
        if (NetworkUtils.isConnected()) {
            block()
        } else {
            apiError(Exception("网络异常，请检查网络连接"))
        }
    }
}
