package com.xueh.comm_core.base.mvvm

import com.xueh.comm_core.net.coroutinedsl.RequestViewModel

/**
 * 在 [RequestViewModel] 之上封装 **Loading 显隐**。
 */
abstract class BaseViewModel : RequestViewModel() {
    fun showLoading() {
        apiLoadingState.value = true
    }

    fun hideLoading() {
        apiLoadingState.value = false
    }
}