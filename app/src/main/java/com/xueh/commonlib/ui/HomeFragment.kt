package com.xueh.commonlib.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import com.xueh.comm_core.base.mvvm.MVVMFragment
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class HomeFragment : MVVMFragment<HomeViewModel>() {
    override fun initListener() {
        tv_home.setOnClickListener{
            VM.loadData()
        }
    }

    override fun getLayoutId()= R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        VM.loadData()
    }

    override fun initDataAfterView() {
    }

    override fun initViewModel(): HomeViewModel = getViewModel()

    override fun initLivedata(viewModel: HomeViewModel) {
        viewModel.banner.observe(this, Observer {
            tv_home.text=it.toString()
        })
    }
}