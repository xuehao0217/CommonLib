package com.xueh.comm_core.net.livedata

import androidx.lifecycle.Observer

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 19:05
 * 备注：
 */
class BaseLifecycleObserver<T> :Observer<T>{
    override fun onChanged(t: T) {

    }
}