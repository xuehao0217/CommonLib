package com.xueh.commonlib.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.xueh.commonlib.R

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/19
 * 备注：
 */


@Composable
fun BarPage() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        // ToolBar 最大向上位移量
        // 56.dp 参考自 androidx.compose.material AppBar.kt 里面定义的 private val AppBarHeight = 56.dp
        val maxUpPx = with(LocalDensity.current) {
            200.dp.roundToPx().toFloat() - 56.dp.roundToPx().toFloat()
        }
        // ToolBar 最小向上位移量
        val minUpPx = 0f
        // 偏移折叠工具栏上移高度
        val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
        // 现在，让我们创建与嵌套滚动系统的连接并聆听子 LazyColumn 中发生的滚动
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffset = toolbarOffsetHeightPx.value + delta
                    toolbarOffsetHeightPx.value = newOffset.coerceIn(-maxUpPx, minUpPx)
                    return Offset.Zero
                }
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                // attach as a parent to the nested scroll system
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyColumn(contentPadding = PaddingValues(top = 200.dp)) {
                items(100) { index ->
                    Text("I'm item $index", modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp))
                }
            }
            ScrollableAppBar(
                title = "toolbar offset is ${toolbarOffsetHeightPx.value}",
                scrollableAppBarHeight = 200.dp,
                toolbarOffsetHeightPx = toolbarOffsetHeightPx,
                backgroundImageId = R.drawable.ic_launcher
            )
        }
    }
}





@Composable
fun ScrollableAppBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = com.xueh.comm_core.R.string.app_name), //默认为应用名
    navigationIcon: @Composable (() -> Unit) =
        { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "ArrowBack",tint = Color.White) }, //默认为返回键
    @DrawableRes backgroundImageId:Int, // 背景图片
    background: Color = MaterialTheme.colors.primary,
    scrollableAppBarHeight: Dp, //ScrollableAppBar高度
    toolbarOffsetHeightPx: MutableState<Float> //向上偏移量
) {

    // 应用栏最大向上偏移量
    val maxOffsetHeightPx = with(LocalDensity.current) { scrollableAppBarHeight.roundToPx().toFloat() - toolBarHeight.roundToPx().toFloat() }
    // Title 偏移量参考值
    val titleOffsetWidthReferenceValue = with(LocalDensity.current) { navigationIconSize.roundToPx().toFloat() }

    Box(modifier = Modifier
        .height(scrollableAppBarHeight)
        .offset {
            IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) //设置偏移量
        }
        .fillMaxWidth()
    ) {
        Image(painter = painterResource(id = backgroundImageId), contentDescription = "background", contentScale = ContentScale.FillBounds)
        // 自定义应用栏
        Row(
            modifier = modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = -toolbarOffsetHeightPx.value.roundToInt() //保证应用栏是始终不动的
                    )
                }
                .height(toolBarHeight)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 导航图标
            Box(modifier = Modifier.size(navigationIconSize),contentAlignment = Alignment.Center) {
                navigationIcon()
            }
        }

        Box(
            modifier = Modifier
                .height(toolBarHeight) //和ToolBar同高
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .offset {
                    IntOffset(
                        x = -((toolbarOffsetHeightPx.value / maxOffsetHeightPx) * titleOffsetWidthReferenceValue).roundToInt(),
                        y = 0
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = title,color = Color.Blue,modifier = Modifier.padding(start = 20.dp),fontSize = 20.sp)
        }
    }
}

// 应用栏高度
private val toolBarHeight = 56.dp
// 导航图标大小
private val navigationIconSize = 50.dp
