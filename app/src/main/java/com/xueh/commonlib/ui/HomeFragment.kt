package com.xueh.commonlib.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.net.ServiceGenerator
import com.xueh.comm_core.utils.rx.rxjava.RxJavaUtils
import com.xueh.commonlib.R
import com.xueh.commonlib.api.RestApi
import kotlinx.android.synthetic.main.fragment_home.*


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
        bindStateView(tv_home).changeUiStatusIgnore(UiStatus.LOADING)
        RxJavaUtils.delay(3) {
            showState(UiStatus.CONTENT)
        }
        ServiceGenerator.getService(RestApi::class.java)
            .bannerList().observe(this, Observer {
                Log.e("http", "res:${it.data}")
            })
    }

    override fun initDataAfterView() {
    }

}