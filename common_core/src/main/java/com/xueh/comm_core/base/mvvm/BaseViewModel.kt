package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.helper.launchSafety
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.coroutinedsl.RequestViewModel
import com.xueh.comm_core.net.coroutinedsl.ViewModelDsl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

/**
 * 在 [RequestViewModel] 之上封装 **Loading 显隐**：同时更新 [apiLoading]（LiveData）与 [apiLoadingState]（Compose State）。
 */
abstract class BaseViewModel : RequestViewModel() {
    fun showLoading() {
        apiLoading.postValue(true)
        apiLoadingState.value=true
    }

    fun hideLoading() {
        apiLoading.postValue(false)
        apiLoadingState.value=false
    }
}