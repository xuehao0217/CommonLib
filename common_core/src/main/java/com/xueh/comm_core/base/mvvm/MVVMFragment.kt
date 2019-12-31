package com.xueh.comm_core.base.mvvm

import androidx.lifecycle.Observer
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.net.livedata.StateLiveData

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 15:53
 * 备注：
 */
abstract class MVVMFragment<VM : AbsViewModel> : DFragment() {
    val VM by lazy {
        initViewModel()
    }

    abstract fun initViewModel(): VM

    override fun initDataBeforeView() {
        super.initDataBeforeView()
        VM.VMStateLiveData.state.observe(this, Observer {
            if (it == StateLiveData.State.Loading) {
                showProgressDialog()
            } else {
                hideProgressDialog()
            }
        })
    }
}