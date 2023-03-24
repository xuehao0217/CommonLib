package com.xueh.comm_core.base.mvvm

import android.util.Log
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.ViewModelHelper

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 14:04
 * 备注：
 */
abstract class MVVMComposeActivity<VM : AbsViewModel> : BaseComposeActivity() {
    lateinit var viewModel: VM
    override fun initView() {
        viewModel = ViewModelHelper.getViewModel(this.javaClass, this)
        viewModel.apiLoading.observe(this) {
            it?.let {
                if (it) showProgressDialog() else hideProgressDialog()
            }
        }

        viewModel.apiException.observe(this) {
            Log.e("MVVMComposeActivity", "BaseViewModel--> $it")
        }
    }
}