package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 15:53
 * 备注：
 */
abstract class MVVMFragment<VB:ViewBinding,viewModel : AbsViewModel> : DFragment<VB>() {
    val viewModel by lazy {
        initViewModel()
    }

    abstract fun initViewModel(): viewModel
    abstract fun initLivedata(viewModel: viewModel)

    override fun initDataBeforeView() {
        initLivedata(viewModel)
        super.initDataBeforeView()
        viewModel.apiLoading.observe(this, Observer {
            it?.let {
                if(it) showProgressDialog() else hideProgressDialog()
            }
        })

        viewModel.apiException.observe(this, Observer {
            it?.let {
                Log.e("BaseViewModel--> ", it?.toString())
            }
        })
    }
}