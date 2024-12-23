package com.xueh.commonlib.ui

import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.lt.compose_views.nav.PagerNav
import com.lt.compose_views.nav.PagerNavState
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.weight.compose.BoxWrapper
import com.xueh.comm_core.weight.compose.Nav
import com.xueh.comm_core.weight.compose.NavData
import com.xueh.comm_core.weight.compose.NavPage
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.compose.NavPage1
import com.xueh.commonlib.ui.compose.NavPage2
import com.xueh.commonlib.ui.compose.NavPage3

/**
 * 创 建 人: xueh
 * 创建日期: 2023/8/30
 * 备注： https://github.com/KatieBarnett/Experiments/tree/main/jc-edge-to-edge?source=post_page-----bea553dd97ff--------------------------------
 */
class MainComposeActivity : BaseComposeActivity() {
    companion object {
        var interceptTab by mutableStateOf(false)
        var showRedPoint = mutableStateOf(false)
    }

    override fun showTitleView() = false

    private var backPressedTime: Long = 0

    val pages = mutableListOf(
        HomePage(),
        MinePage(),
        TabPage3(),
    )

    val navList = mutableListOf(
        NavData(
            selectIcon = R.mipmap.ic_home_select,
            unSelectIcon = R.mipmap.ic_home_normal,
            text = "首页",
        ),
        NavData(
            selectIcon = R.mipmap.ic_my_select,
            unSelectIcon = R.mipmap.ic_my_normal,
            text = "我的",
            showRed = showRedPoint
        ),
        NavData(
            selectIcon = R.mipmap.ic_my_select,
            unSelectIcon = R.mipmap.ic_my_normal,
            text = "拦截"
        ),
    )

    val interceptPos = 2

    @Composable
    override fun setComposeContent() {
        NavPage(pages = pages, navList = navList, interceptClick = {
            ToastUtils.showShort("interceptClick")
            if (interceptTab) {
                interceptPos
            } else {
                -1
            }
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime <= 2000) {
                    AppUtils.exitApp()
                } else {
                    backPressedTime = System.currentTimeMillis()
                    ToastUtils.showShort("Press again to exit the app")
                }
            }
        })
    }
}

