package com.xueh.commonlib.ui

import android.view.MenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ActivityUtils
import com.lt.compose_views.compose_pager.ComposePagerScope
import com.lt.compose_views.nav.NavContent
import com.xueh.comm_core.base.compose.theme.AppBaseTheme
import com.xueh.comm_core.base.compose.theme.AppThemeColorType
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.appThemeType
//import com.xueh.comm_core.base.compose.theme.AppBaseTheme
//import com.xueh.comm_core.base.compose.theme.AppThemeType
//import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.web.AgentComposeWebActivity
import com.xueh.comm_core.weight.compose.BoxWrapper
import com.xueh.commonlib.ui.compose.ItemView
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.xml.MainActivity

class MinePage : NavContent {
    override val route: String = "MinePage"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        Column(Modifier.statusBarsPadding()) {
            ItemView("XML 页面") {
                ActivityUtils.startActivity(MainActivity::class.java)
            }
            ItemView("是否拦截第三个Tab ${MainComposeActivity.interceptTab}") {
                MainComposeActivity.interceptTab = !MainComposeActivity.interceptTab
            }
            ItemView("是否显示小红点 ${MainComposeActivity.showRedPoint.value}") {
                MainComposeActivity.showRedPoint.value = !MainComposeActivity.showRedPoint.value
            }
            ItemView("AgentComposeWeb") {
                AgentComposeWebActivity.start("https://www.baidu.com?hideTitle=1", "百度")
            }
        }
    }
}

@Composable
fun ItemBox(string: String, clickEvent: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(AppBaseTheme.colors.background)
            .clickable(onClick = clickEvent)
            .fillMaxWidth()
            .height(50.dp)
            .border(
                1.5.dp,
                AppBaseTheme.colors.theme,
                shape = CircleShape
            ),

        contentAlignment = Alignment.Center
    ) {
        Text(
            text = string,
            color = AppBaseTheme.colors.title,
            textAlign = TextAlign.Center,
        )
    }
}


class TabPage3 : NavContent {
    override val route: String = "TabPage3"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        Column(Modifier.statusBarsPadding()) {
            ItemView("修改Theme Dark") {
                appThemeType = AppThemeType.Dark
            }
            ItemView("修改Theme Light") {
                appThemeType = AppThemeType.Light
            }
            ItemView("修改Theme FOLLOW_SYSTEM") {
                appThemeType = AppThemeType.FOLLOW_SYSTEM
            }
        }
    }
}