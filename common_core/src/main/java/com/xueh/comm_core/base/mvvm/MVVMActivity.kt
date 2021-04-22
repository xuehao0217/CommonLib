package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 14:04
 * 备注：
 */
abstract class MVVMActivity<VB:ViewBinding,VM : AbsViewModel> : DActivity<VB>() {
    val VM by lazy {
        initViewModel()
    }

    abstract fun initViewModel(): VM
    abstract fun initLiveData(viewModel: VM)

    override fun initDataBeforeView() {
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