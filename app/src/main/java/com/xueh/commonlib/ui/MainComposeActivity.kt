package com.xueh.commonlib.ui

import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.weight.compose.BottomNavPager
import com.xueh.comm_core.weight.compose.NavData
import com.xueh.commonlib.R
import kotlinx.coroutines.delay

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

    @Composable
    override fun setComposeContent() {
        // 页面列表
        val pages = listOf<@Composable () -> Unit>(
            { HomePage() },
            { MinePage() },
            { TabPage3() }
        )

        // 导航列表
        val navItems = remember {
            mutableStateListOf(
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
        }

        // 模拟红点显示
        LaunchedEffect(Unit) {
            delay(10000)
            navItems[1].showRed.value = true
        }

        BottomNavPager(
            pages = pages,
            navItems = navItems,
            selectTextColor = Color.Red,
            unSelectTextColor = Color.Gray,
            fontSize = 12.sp,
            interceptClick = { index ->
                if (interceptTab && index == 2) {
                    true
                } else {
                    false
                }
            }
        )

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

