package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.Observer
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel

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