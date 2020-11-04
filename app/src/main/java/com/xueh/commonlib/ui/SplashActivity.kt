package com.xueh.commonlib.ui

import android.content.Intent
import android.os.Bundle
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.utils.rx.rxjava.RxJavaUtils
import com.xueh.commonlib.R


class SplashActivity : DActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        addDisposable(RxJavaUtils.delay(2) {
            startActivity(MainActivity::class.java)
            this@SplashActivity.finish()
        })
    }

    override fun initDataAfterView() {
    }

    override fun initListener() {
    }

    override fun getLayoutId() = R.layout.activity_splash
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppSplash)
        super.onCreate(savedInstanceState)
    }
}
