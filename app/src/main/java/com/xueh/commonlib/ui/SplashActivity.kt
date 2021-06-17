package com.xueh.commonlib.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.utils.time.Interval
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.ActivitySplashBinding
import java.util.concurrent.TimeUnit


class SplashActivity : DActivity<ActivitySplashBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        Interval(
            1,
            1,
            TimeUnit.SECONDS,
            0
        ).life(this) // 自定义计数器个数的轮循器, 当[start]]比[end]值大, 且end不等于-1时, 即为倒计时
            .finish {
                startActivity(MainActivity::class.java)
                this@SplashActivity.finish()
            }
            .start()
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
