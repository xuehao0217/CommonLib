package com.xueh.comm_core.base.mvvm

import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.base.BaseFragment
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel


/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 15:53
 * 备注：
 */
abstract class MVVMFragment<VB : ViewBinding, VM : AbsViewModel> : DFragment<VB>() {
    lateinit var viewModel: VM

    abstract fun initViewModel(): VM

    abstract fun initLivedata(viewModel: VM)

    override fun initDataBeforeView() {
        viewModel = initViewModel()
        initLivedata(viewModel)
        super.initDataBeforeView()
        viewModel.apiLoading.observe(this, Observer {
            it?.let {
                if (it) showProgressDialog() else hideProgressDialog()
            }
        })

        viewModel.apiException.observe(this, Observer {
            it?.let {
                Log.e("BaseViewModel--> ", it?.toString())
            }
        })
    }

}