package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.ViewModelHelper

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 14:04
 * 备注：
 */
abstract class MVVMActivity<VB : ViewBinding, VM : AbsViewModel> : DActivity<VB>() {
    lateinit var viewModel: VM

    abstract fun initLiveData(viewModel: VM)

    override fun initDataBeforeView() {
        viewModel = ViewModelHelper.getViewModel(this.javaClass, this)
        initLiveData(viewModel)
        super.initDataBeforeView()
        viewModel.apiLoading.observe(this) {
            it?.let {
                if (it) showProgressDialog() else hideProgressDialog()
            }
        }

        viewModel.apiException.observe(this) {
            it?.let {
                Log.e("BaseViewModel--> ", it?.toString())
            }
        }
    }
}