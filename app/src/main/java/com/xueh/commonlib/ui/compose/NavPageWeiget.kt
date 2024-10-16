package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.blankj.utilcode.util.ToastUtils
import com.lt.compose_views.compose_pager.ComposePagerScope
import com.lt.compose_views.nav.NavContent
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.weight.compose.NavData
import com.xueh.comm_core.weight.compose.NavPage
import com.xueh.commonlib.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 创 建 人: xueh
 * 创建日期: 2023/8/30
 * 备注：
 */
@ExperimentalFoundationApi
@Preview
@Composable
fun NavPageWeigetPage() {
    var  showRed = rememberMutableStateOf {
        false
    }
    val navList = mutableListOf(
        NavData(selectIcon = R.mipmap.ic_home_select, unSelectIcon = R.mipmap.ic_home_normal, text = "首页",showRed=showRed),
        NavData(selectIcon = R.mipmap.ic_my_select, unSelectIcon = R.mipmap.ic_my_normal, text = "我的"),
        NavData(selectIcon = R.mipmap.ic_my_select, unSelectIcon = R.mipmap.ic_my_normal, text = "我的2"),
    )
    val pages = mutableListOf(NavPage1(),NavPage2(),NavPage3())

   val interceptPos =1
    NavPage(pages = pages, navList = navList, interceptClick = {
        ToastUtils.showShort("interceptClick")

        interceptPos
    })
    
    LaunchedEffect(Unit) {
        delay(4000)
        showRed.value=!showRed.value
        delay(4000)
        showRed.value=!showRed.value
    }
}


class NavPage1 : NavContent {
    override val route: String="NavPage1"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Green))
    }
}

class NavPage2 : NavContent {
    override val route: String="NavPage2"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Red))
    }
}

class NavPage3 : NavContent {
    override val route: String="NavPage3"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow))
    }
}