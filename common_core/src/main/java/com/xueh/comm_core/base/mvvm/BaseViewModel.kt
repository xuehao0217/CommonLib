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
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 11:56
 * 备注：
 */
abstract class BaseViewModel<E> : RequestViewModel() {
    protected val api by lazy {
        initApi()
    }

    protected abstract fun initApi(): E

    protected fun showLoading() {
        apiLoading.postValue(true)
    }

    protected fun hideLoading() {
        apiLoading.postValue(false)
    }
}