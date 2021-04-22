package com.xueh.commonlib.ui

import android.content.Intent
import android.os.Bundle
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.utils.rx.rxjava.RxJavaUtils
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.ActivitySplashBinding


class SplashActivity : DActivity<ActivitySplashBinding>() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppStyle)
        super.onCreate(savedInstanceState)
    }
}
