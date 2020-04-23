package com.xueh.comm_core.base.mvvm

import com.xueh.comm_core.net.coroutine.RequestViewModel


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

    private fun showLoading() {
        apiLoading.postValue(true)
    }

    private fun hideLoading() {
        apiLoading.postValue(false)
    }


}