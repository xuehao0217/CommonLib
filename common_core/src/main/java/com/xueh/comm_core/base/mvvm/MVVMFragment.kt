package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.Observer
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel

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
    abstract fun initLivedata(viewModel: VM)

    override fun initDataBeforeView() {
        initLivedata(VM)
        super.initDataBeforeView()
        VM.apiLoading.observe(this, Observer {
            it?.let {
                if(it) showProgressDialog() else hideProgressDialog()
            }
        })

        VM.apiException.observe(this, Observer {
            it?.let {
                Log.e("BaseViewModel--> ", it?.toString())
            }
        })
    }
}