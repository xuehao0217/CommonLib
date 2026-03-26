package com.xueh.comm_core.base.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.widget.ComposeLoadingDialog
import kotlinx.coroutines.flow.filterNotNull

/**
 * Compose ViewModel 包装器
 *
 * 自动创建 ViewModel 实例，并绑定 Loading 弹窗和异常收集。
 *
 * 流程：
 *   1. 通过 viewModel() 获取/创建 ViewModel 实例
 *   2. 调用 content 渲染 UI
 *   3. 绑定 ComposeLoadingDialog，由 apiLoadingState 控制显隐
 *   4. 通过 snapshotFlow 收集 apiExceptionState，触发 onError 回调
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
    content(viewModel)
    ComposeLoadingDialog(alertDialog = viewModel.apiLoadingState)

    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.apiExceptionState.value }
            .filterNotNull()
            .collect { error ->
                LogUtils.iTag("BaseComposeViewModel", "apiExceptionState===$error")
                onError?.invoke(error)
                viewModel.apiExceptionState.value = null
            }
    }
}
