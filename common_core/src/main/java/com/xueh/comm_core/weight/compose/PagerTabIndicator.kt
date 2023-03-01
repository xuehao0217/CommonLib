package com.xueh.comm_core.weight.compose

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

/**
 * 创 建 人: xueh
 * 创建日期: 2023/3/1
 * 备注：
 */
/**
 * PagerTap 指示器
 * @param  percent  指示器占用整个tab宽度的比例
 * @param  height   指示器的高度
 * @param  color    指示器的颜色
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerTabIndicator(
    tabPositions: List<TabPosition>,
    pagerState: PagerState,
    color: Color = MaterialTheme.colors.primarySurface,
    @FloatRange(from = 0.0, to = 1.0) percent: Float = 0.4f,
    height: Dp = 4.dp,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentPage = minOf(tabPositions.lastIndex, pagerState.currentPage)
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffset

        val indicatorWidth = currentTab.width.toPx() * percent

        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).toPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).toPx()
        } else {
            currentTab.left.toPx()
        }
        val canvasHeight = size.height
        drawRoundRect(
            color = color,
            topLeft = Offset(
                indicatorOffset + (currentTab.width.toPx() * (1 - percent) / 2),
                canvasHeight - height.toPx()
            ),
            size = Size(indicatorWidth + indicatorWidth * Math.abs(fraction), height.toPx()),
            cornerRadius = CornerRadius(50f)
        )
    }
}

/**
 * 自定义 PagerTab
 * @param index                     对应第几个tab 从0开始
 * @param pageCount                 page的总个数
 * @param selectedContentColor      tab选中时的颜色
 * @param unselectedContentColor    tab没选中时的颜色
 * @param selectedFontSize          tab选中时的文字大小
 * @param unselectedFontSize        tab没选中时的文字大小
 * @param selectedFontWeight        tab选中时的文字比重
 * @param unselectedFontWeight      tab没选中时的文字比重
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerTab(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    index: Int,
    pageCount: Int,
    text: String,
    selectedContentColor: Color = MaterialTheme.colors.primary,
    unselectedContentColor: Color = MaterialTheme.colors.onSurface,
    selectedFontSize: TextUnit = 18.sp,
    unselectedFontSize: TextUnit = 15.sp,
    selectedFontWeight: FontWeight = FontWeight.Bold,
    unselectedFontWeight: FontWeight = FontWeight.Normal,
) {
    val previousIndex = Math.max(index - 1, 0)
    val nextIndex = Math.min(index + 1, pageCount - 1)
    val currentIndexPlusOffset = pagerState.currentPage + pagerState.currentPageOffset

    val progress =
        if (currentIndexPlusOffset >= previousIndex && currentIndexPlusOffset <= nextIndex) {
            1f - Math.abs(index - currentIndexPlusOffset)
        } else {
            0f
        }

    val fontSize = lerp(unselectedFontSize, selectedFontSize, progress)
    val fontWeight =
        androidx.compose.ui.text.font.lerp(unselectedFontWeight, selectedFontWeight, progress)
    val color = lerp(unselectedContentColor, selectedContentColor, progress)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = color, fontSize = fontSize, fontWeight = fontWeight)
    }
}
