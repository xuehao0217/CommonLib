package com.xueh.commonlib

import com.fengchen.uistatus.UiStatusManager
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.BaseApplication
import com.xueh.comm_core.net.IHttpBaseUrl
import com.xueh.comm_core.net.ServiceGenerator


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 12:52
 * 备注：
 */
class MyApplication : BaseApplication(), IHttpBaseUrl {
    override fun onCreate() {
        super.onCreate()
        initState()
        ServiceGenerator.setBaeUrl(this)
    }

    private fun initState() {
        UiStatusManager.getInstance()
            .addUiStatusConfig(UiStatus.LOADING, R.layout.state_loading)
            .addUiStatusConfig(UiStatus.EMPTY, R.layout.state_empty)
            .addUiStatusConfig(UiStatus.NETWORK_ERROR, R.layout.state_net_error)
    }

    override fun getHttpBaseUrl() = "https://www.wanandroid.com/"
}