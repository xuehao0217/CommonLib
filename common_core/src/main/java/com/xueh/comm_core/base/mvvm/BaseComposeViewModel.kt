package com.xueh.comm_core.base.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.components.ComposeLoadingDialog
import kotlinx.coroutines.flow.filterNotNull

/**
 * Compose ViewModel 包装器
 *
 * 自动创建 ViewModel 实例，并绑定 Loading 弹窗和异常收集。
 *
 * ## 组合内流程
 * 1. 通过 [viewModel] 获取/创建 ViewModel 实例。
 * 2. 调用 [content] 渲染页面主体。
 * 3. [ComposeLoadingDialog] 绑定 [com.xueh.comm_core.base.mvvm.AbsViewModel.apiLoadingState]。
 * 4. 在 **Lifecycle STARTED 及以上** 时，通过 [snapshotFlow] 订阅 [com.xueh.comm_core.base.mvvm.AbsViewModel.apiExceptionState]，
 *    收到非空异常则触发 [onError] 并清空状态；进入 ON_STOP 后停止收集，避免后台无意义订阅。
 *
 * @param onError 异常回调，默认弹 Toast 提示。传 null 可禁用。
 * @param content 页面主体内容，接收 ViewModel 实例
 */
@Composable
inline fun <reified V : BaseViewModel> BaseComposeViewModel(
    noinline onError: ((Throwable) -> Unit)? = { ToastUtils.showShort(it.message ?: "请求异常") },
    content: @Composable (V) -> Unit,
) {
    val viewModel: V = viewModel()
    val currentOnError = rememberUpdatedState(onError)
    val lifecycleOwner = LocalLifecycleOwner.current
    content(viewModel)
    ComposeLoadingDialog(alertDialog = viewModel.apiLoadingState)

    LaunchedEffect(viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            snapshotFlow { viewModel.apiExceptionState.value }
                .filterNotNull()
                .collect { error: Throwable ->
                    LogUtils.iTag("BaseComposeViewModel", "apiExceptionState===$error")
                    currentOnError.value?.invoke(error)
                    viewModel.apiExceptionState.value = null
                }
        }
    }
}
