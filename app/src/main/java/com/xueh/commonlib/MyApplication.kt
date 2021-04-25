package com.xueh.commonlib

import com.fengchen.uistatus.UiStatusManager
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.BaseApplication
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 12:52
 * 备注：
 */
class MyApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        initState()

        HttpRequest.apply {
            setBaseUrl("https://www.wanandroid.com/")
            putHead("name","xuehao")
        }

//        startKoin {
//            androidContext(this@MyApplication)
//            modules(viewModelModule)
//        }
    }

    private fun initState() {
        UiStatusManager.getInstance()
            .addUiStatusConfig(UiStatus.LOADING, R.layout.state_loading)
            .addUiStatusConfig(UiStatus.EMPTY, R.layout.state_empty)
            .addUiStatusConfig(UiStatus.NETWORK_ERROR, R.layout.state_net_error)
    }
}