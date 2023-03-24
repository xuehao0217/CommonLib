package com.xueh.comm_core.weight.compose.refreshheader

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ConvertUtils
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag

/**
 * 创 建 人: xueh
 * 创建日期: 2022/9/5
 * 备注：
 */
@Composable
fun RefreshHeader(state: SmartSwipeRefreshState) {
    val text by remember {
        derivedStateOf {
            when (state.refreshFlag) {
                SmartSwipeStateFlag.TIPS_DOWN -> "下拉刷新"
                SmartSwipeStateFlag.REFRESHING -> "正在刷新数据…"
                SmartSwipeStateFlag.TIPS_RELEASE -> "松开立刻刷新数据…"
                else -> "刷新完成"
            }
        }
    }
    val isRefreshing by remember {
        derivedStateOf { state.refreshFlag == SmartSwipeStateFlag.REFRESHING }
    }
    val show by remember {
        derivedStateOf {
            state.indicatorOffset.value / ConvertUtils.dp2px(35f)
        }
    }
    Box(
        modifier = Modifier
            .alpha(show)
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
//            if (isRefreshing) {
//                AnimLoading(id = R.drawable.brvah_sample_footer_loading, size = 44)
//            } else {
//                //refresh_down
//                ImageCompose(id = R.drawable.brvah_sample_footer_loading)
//            }
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .wrapContentSize()
                    .clipToBounds()
                    .padding(16.dp, 0.dp)
            )
        }
    }
}