package com.xueh.comm_core.weight.compose.refreshheader

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ConvertUtils
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import java.text.SimpleDateFormat
import java.util.Locale

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
            state.indicatorOffset / ConvertUtils.dp2px(35f)
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


@Composable
fun MyRefreshHeader(state: SmartSwipeRefreshState, isNeedTimestamp: Boolean = true) {
    var flag = state.refreshFlag
    var lastRecordTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
    ) {
        val refreshAnimate by rememberInfiniteTransition(label = "MyRefreshHeader").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing)),
            label = "MyRefreshHeader"
        )
        val transitionState = remember { MutableTransitionState(0) }
        val transition = updateTransition(transitionState, label = "arrowTransition")
        val arrowDegrees by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 500) }, label = "arrowDegrees"
        ) {
            if (it == 0) 0f else 180f
        }
        transitionState.targetState = if (flag == SmartSwipeStateFlag.TIPS_RELEASE) 1 else 0
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.rotate(if (flag == SmartSwipeStateFlag.REFRESHING) refreshAnimate else arrowDegrees),
                imageVector = when (flag) {
                    SmartSwipeStateFlag.IDLE -> Icons.Default.KeyboardArrowDown
                    SmartSwipeStateFlag.REFRESHING -> Icons.Default.Refresh
                    SmartSwipeStateFlag.SUCCESS -> {
                        lastRecordTime = System.currentTimeMillis()
                        Icons.Default.Done
                    }

                    SmartSwipeStateFlag.ERROR -> {
                        lastRecordTime = System.currentTimeMillis()
                        Icons.Default.Warning
                    }

                    SmartSwipeStateFlag.TIPS_DOWN -> Icons.Default.KeyboardArrowDown
                    SmartSwipeStateFlag.TIPS_RELEASE -> Icons.Default.KeyboardArrowDown
                },
                contentDescription = null
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                androidx.compose.material3.Text(
                    text = when (flag) {
                        SmartSwipeStateFlag.REFRESHING -> "刷新中..."
                        SmartSwipeStateFlag.SUCCESS -> "刷新成功"
                        SmartSwipeStateFlag.ERROR -> "刷新失败"
                        SmartSwipeStateFlag.IDLE, SmartSwipeStateFlag.TIPS_DOWN -> "下拉可以刷新"
                        SmartSwipeStateFlag.TIPS_RELEASE -> "释放立即刷新"
                    }, fontSize = 18.sp
                )
                if (isNeedTimestamp) {
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material3.Text(
                        text = "上次刷新：${
                            SimpleDateFormat(
                                "MM-dd HH:mm",
                                Locale.getDefault()
                            ).format(lastRecordTime)
                        }", fontSize = 14.sp
                    )
                }
            }
        }
    }
}
