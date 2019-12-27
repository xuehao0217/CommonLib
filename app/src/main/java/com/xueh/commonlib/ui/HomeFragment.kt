package com.xueh.commonlib.ui

import android.os.Bundle
import android.view.View
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.utils.rx.rxjava.RxJavaUtils
import com.xueh.commonlib.R
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
    }

    override fun initDataAfterView() {
    }

}