package com.xueh.comm_core.base.xml

import android.os.Bundle
import android.util.Log
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.base.mvvm.AbsViewModel
import com.xueh.comm_core.helper.xml.ViewModelHelper

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 14:04
 * 备注：
 */
abstract class MVVMActivity<VB : ViewBinding, VM : AbsViewModel> : DActivity<VB>() {
    lateinit var viewModel: VM

    abstract fun initLiveData(viewModel: VM)

    override fun initView(savedInstanceState: Bundle?) {
        viewModel = ViewModelHelper.getViewModel(this.javaClass, this)
        initLiveData(viewModel)
        viewModel.apiLoading.observe(this) {
            it?.let {
                if (it) showProgressDialog() else hideProgressDialog()
            }
        }

        viewModel.apiException.observe(this) {
            it?.let {
                Log.e("MVVMActivity", "BaseViewModel--> $it")
            }
        }
    }
}