package com.xueh.commonlib.ui

import android.os.Bundle
import android.view.View
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.utils.rx.rxjava.RxJavaUtils
import com.xueh.commonlib.R


class SplashActivity : DActivity() {

    override fun initView(inflateView: View?, savedInstanceState: Bundle?) {
        addDisposable(RxJavaUtils.delay(2){
            startActivity(MainActivity::class.java)
            this@SplashActivity.finish()
        })
    }

    override fun initDataAfterView() {
    }

    override fun initListener() {
    }

    override fun getCreateViewLayoutId()= R.layout.activity_splash
}
