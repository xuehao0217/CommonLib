package com.xueh.comm_core.weight.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xueh.comm_core.base.mvvm.BaseViewModel


@Composable
inline fun <reified V : BaseViewModel> BaseComposeViewModel(content: @Composable (V) -> Unit) {
    var viewModel: V = viewModel()
    content(viewModel)
    ComposeLoadingDialog(alertDialog = viewModel.apiComposeLoading)
}