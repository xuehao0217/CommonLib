package com.xueh.comm_core.base.mvvm

import androidx.lifecycle.Observer
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.net.livedata.StateLiveData

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 14:04
 * 备注：
 */
abstract class MVVMActivity<VM : AbsViewModel> : DActivity() {
    val VM by lazy {
        CreateViewModel()
    }

    abstract fun CreateViewModel(): VM

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