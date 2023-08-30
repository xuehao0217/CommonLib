package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.weight.compose.NavData
import com.xueh.comm_core.weight.compose.NavPage
import com.xueh.commonlib.R

/**
 * 创 建 人: xueh
 * 创建日期: 2023/8/30
 * 备注：
 */
@ExperimentalFoundationApi
@Preview
@Composable
fun NavPageWeigetPage() {
    var navList = mutableListOf(
        NavData(selectIcon = R.mipmap.ic_home_select, unSelectIcon = R.mipmap.ic_home_normal, text = "首页", showRed = false),
        NavData(selectIcon = R.mipmap.ic_my_select, unSelectIcon = R.mipmap.ic_my_normal, text = "我的", showRed = false),
    )
    NavPage(interceptIndex = 0, navList = navList, interceptClick = {
        ToastUtils.showShort("interceptClick")
    }) {
        Box(
            contentAlignment=Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when (it) {
                        0 -> {
                            Color.Blue
                        }

                        else -> {
                            Color.Green
                        }
                    }
                )
        ){
            Text(text = "${it}", fontSize = 18.sp, color = Color.White)
        }
    }
}