package com.xueh.comm_core.base.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.weight.compose.ComposeLoadingDialog


@Composable
inline fun <reified V : BaseViewModel> BaseComposeViewModel(content: @Composable (V) -> Unit) {
    var viewModel: V = viewModel()
    content(viewModel)
    ComposeLoadingDialog(alertDialog = viewModel.apiLoadingState)

    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.apiExceptionState.value }
            .collect {
                LogUtils.iTag("BaseComposeViewModel","apiExceptionState===${it}")
            }
    }

}