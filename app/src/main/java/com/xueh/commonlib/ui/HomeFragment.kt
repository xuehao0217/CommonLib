package com.xueh.commonlib.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.ServiceGenerator
import com.xueh.comm_core.net.coroutine.getNetData
import com.xueh.commonlib.R
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO
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

        val homeViewModel=  ViewModelProvider.AndroidViewModelFactory.getInstance(Utils.getApp()).create(HomeViewModel::class.java)
//        val homeViewModel = ViewModelProviders.of(this)[HomeViewModel::class.java] //这方法已经过期
        homeViewModel.banners.observe(this, Observer {
            ToastUtils.showShort(it.toString())
        })
        homeViewModel.loadData()





    }


    override fun initDataAfterView() {
    }

}