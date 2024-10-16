package com.xueh.commonlib.ui

import android.view.MenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.ActivityUtils
import com.lt.compose_views.compose_pager.ComposePagerScope
import com.lt.compose_views.nav.NavContent
import com.xueh.commonlib.ui.compose.ItemView
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.xml.MainActivity

class MinePage : NavContent {
    override val route: String = "MinePage"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        Column(Modifier.statusBarsPadding()) {
            ItemView("XML 页面", false) {
                ActivityUtils.startActivity(MainActivity::class.java)
            }
            ItemView("是否拦截第三个Tab ${MainComposeActivity.interceptTab}" ) {
                MainComposeActivity.interceptTab = !MainComposeActivity.interceptTab
            }
            ItemView("是否显示小红点 ${MainComposeActivity.showRedPoint.value}") {
                MainComposeActivity.showRedPoint.value = !MainComposeActivity.showRedPoint.value
            }
        }

    }
}


class TabPage3 : NavContent {
    override val route: String = "TabPage3"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow))
    }
}