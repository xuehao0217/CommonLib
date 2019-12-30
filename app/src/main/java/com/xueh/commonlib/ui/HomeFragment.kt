package com.xueh.commonlib.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.DFragment
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.HomeViewModel


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class HomeFragment : DFragment() {
    override fun initListener() {
    }

    override fun getCreateViewLayoutId() = R.layout.fragment_home

    override fun initView(inflateView: View, savedInstanceState: Bundle?) {
//        bindStateView(tv_home).changeUiStatusIgnore(UiStatus.LOADING)
//        RxJavaUtils.delay(3) {
//            showState(UiStatus.CONTENT)
//        }

        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.banners.observe(this, Observer {
            ToastUtils.showShort(it.toString())
        })
        homeViewModel.loadData()
    }


    override fun initDataAfterView() {
    }

}