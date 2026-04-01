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
 * 网络请求与 **统一 UI 状态**（Loading / 异常 / Toast）的 ViewModel 层。
 *
 * **流程概要**：
 * 1. 入口方法（[apiDSL]、[apiFlow] 等）先经 [runIfNetworkAvailable] 检查网络。
 * 2. 请求前调用 `apiLoading(true)` 更新 [com.xueh.comm_core.base.mvvm.AbsViewModel.apiLoadingState]。
 * 3. 成功或业务错误（[BaseResult] 非成功）走 [apiError] 或自定义 [error]；最终 [apiFinally] 关闭 Loading。
 *
 * 子类典型继承链：[AbsViewModel] → **RequestViewModel** → [com.xueh.comm_core.base.mvvm.BaseViewModel]。
 */
open class RequestViewModel : AbsViewModel() {

    // ------------------- DSL 调用 -------------------

    /** 配置 [ViewModelDsl] 并 [ViewModelDsl.launch]，适合原始类型 [T] 直接返回的场景。 */
    protected fun <T> apiDSL(block: ViewModelDsl<T>.() -> Unit) =
        runIfNetworkAvailable { ViewModelDsl<T>().apply(block).launch(this) }

    /** 同 [apiDSL]，但走 [ViewModelDsl.launchFlow]（Flow 链式收集）。 */
    protected fun <T> apiFlowDSL(block: ViewModelDsl<T>.() -> Unit) =
        runIfNetworkAvailable { ViewModelDsl<T>().apply(block).launchFlow(this) }

    /** 请求体返回 [BaseResult]；由 [ViewModelDsl.launchBaseResult] 解析 [BaseResult.isSuccess]。 */
    protected fun <T> apiDslResult(block: ViewModelDsl<T>.() -> Unit) =
        runIfNetworkAvailable { ViewModelDsl<T>().apply(block).launchBaseResult(this) }

    // ------------------- Flow 简化请求 -------------------

    /**
     * 将 [request] 包装为 Flow，在 IO 上执行；[onStart] 默认触发 Loading，[catch] 默认 [apiError]，[onCompletion] 默认 [apiFinally]。
     * [BaseResult.isSuccess] 为 true 时调用 [result](data)。
     */
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
        apiLoadingState.value = value
    }

    open fun apiError(
        e: Throwable,
        showToast: Boolean = true,
        log: Boolean = true
    ) {
        val ex = e as? Exception ?: Exception(e)
        apiExceptionState.value = ex

        if (log) Log.e("API_ERROR", ex.message ?: "未知错误", ex)
        if (showToast) ToastUtils.showShort(ex.message ?: "网络异常")

        apiFinally()
    }

    open fun apiFinally() {
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
