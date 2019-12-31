package com.xueh.commonlib.ui

import android.os.Bundle
import android.view.View
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
    }

    override fun getCreateViewLayoutId() = R.layout.fragment_home

    override fun initView(inflateView: View, savedInstanceState: Bundle?) {
//        bindStateView(tv_home).changeUiStatusIgnore(UiStatus.LOADING)
//        RxJavaUtils.delay(3) {
//            showState(UiStatus.CONTENT)
//        }

//        val homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(Utils.getApp())
//            .create(HomeViewModel::class.java)
//        val homeViewModel = ViewModelProviders.of(this)[HomeViewModel::class.java] //这方法已经过期

        VM.stateBanner.observe(this, Observer {
            tv_home.text=it.toString()
        })

        VM.loadData()
    }


    override fun initDataAfterView() {
    }


    override fun initViewModel(): HomeViewModel=getViewModel()

//    override fun initViewModel(): HomeViewModel=
//        ViewModelProvider.AndroidViewModelFactory.getInstance(Utils.getApp())
//            .create(HomeViewModel::class.java)
}