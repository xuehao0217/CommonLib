package com.xueh.commonlib

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.xueh.comm_core.base.BaseApplication
import com.xueh.comm_core.net.HttpRequest
import me.jessyan.progressmanager.ProgressManager


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 12:52
 * 备注：
 */
class MyApplication : BaseApplication() {
    override fun init() {
        initLog()
        initNet()
    }

    private fun initNet() {
        val chuckerCollector = ChuckerCollector(
            context = this@MyApplication,
            // Toggles visibility of the notification
            showNotification = true,
            // Allows to customize the retention period of collected data
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        val chuckerInterceptor = ChuckerInterceptor.Builder(this@MyApplication)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .redactHeaders("Auth-Token", "Bearer")
            .alwaysReadResponseBody(true)
            .build()

        HttpRequest.init("https://www.wanandroid.com/"){
            okHttp {
                it.apply {
                    addInterceptor(chuckerInterceptor)
                    ProgressManager.getInstance().with(this)
                        .build()
                }
            }
//            retrofit {
//                it.apply {
//                        addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
//                }
//            }
        }
        HttpRequest.putHead("name","xh")
    }

    private fun initLog() {
        LogUtils.getConfig()
            .setLog2FileSwitch(true)
            .setSaveDays(7)
            .setFilePrefix("log")
            .dir = PathUtils.getExternalAppCachePath()
    }

//    private fun initState() {
//        UiStatusManager.getInstance()
//            .addUiStatusConfig(UiStatus.LOADING, R.layout.state_loading)
//            .addUiStatusConfig(UiStatus.EMPTY, R.layout.state_empty)
//            //添加R.id.tv_net_error 点击事件
//            .addUiStatusConfig(
//                UiStatus.NETWORK_ERROR,
//                R.layout.state_net_error,
//                R.id.tv_net_error,
//                null
//            )
//    }
}