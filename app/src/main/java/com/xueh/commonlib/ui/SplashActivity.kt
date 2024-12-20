package com.xueh.commonlib.ui

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.blankj.utilcode.util.ActivityUtils
import com.drake.interval.Interval
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.base.xml.DActivity
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.ActivitySplashBinding
import com.xueh.commonlib.ui.xml.MainActivity
import java.util.concurrent.TimeUnit


class SplashActivity : BaseComposeActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppStyle)
        super.onCreate(savedInstanceState)
    }

    override fun showTitleView(): Boolean {
        return false
    }
    @Composable
    override fun setComposeContent() {
        Interval(
            0,
            1,
            TimeUnit.SECONDS,
            1
        ).life(this) // 自定义计数器个数的轮循器, 当[start]]比[end]值大, 且end不等于-1时, 即为倒计时
            .subscribe {

            }
            .finish {
                ActivityUtils.startActivity(MainComposeActivity::class.java)
                this@SplashActivity.finish()
            }
            .start()
    }
}
