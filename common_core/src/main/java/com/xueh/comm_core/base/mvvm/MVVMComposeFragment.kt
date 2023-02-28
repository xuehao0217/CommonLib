package com.xueh.comm_core.base.mvvm

import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.BaseComposeFragment
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.ViewModelHelper


/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 15:53
 * 备注：
 */
abstract class MVVMComposeFragment<VM : AbsViewModel> : BaseComposeFragment() {
    lateinit var viewModel: VM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelHelper.getFragmentViewModel(this.javaClass, this)
        viewModel.apiLoading.observe(this) {
            it?.let {
                if (it) showProgressDialog() else hideProgressDialog()
            }
        }

        viewModel.apiException.observe(this) {
            ToastUtils.showShort("${it}")
            Log.e("MVVMFragment","BaseViewModel-->$it" )
        }
    }
}