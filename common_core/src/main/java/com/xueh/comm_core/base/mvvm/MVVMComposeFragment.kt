package com.xueh.comm_core.base.mvvm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ComposeView {
        return super.onCreateView(inflater, container, savedInstanceState)

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