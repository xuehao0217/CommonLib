package com.xueh.comm_core.base.mvvm

import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel


/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 11:56
 * 备注：
 */
abstract class BaseViewModel<E> : AbsViewModel() {
    protected val api by lazy {
        initApi()
    }
    protected abstract fun initApi(): E
}