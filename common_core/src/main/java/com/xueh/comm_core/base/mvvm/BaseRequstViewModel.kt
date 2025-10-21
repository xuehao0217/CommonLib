package com.xueh.comm_core.base.mvvm
/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 11:56
 * 备注：
 */
abstract class BaseRequstViewModel<E> : BaseViewModel() {
    protected val api by lazy {
        initApi()
    }

    protected abstract fun initApi(): E
}