package com.xueh.comm_core.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.lerp as lerpFontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun PagerTabIndicator(
    tabPositions: List<MyTabPosition>,
    pagerState: PagerState,
    color: Color = MaterialTheme.colorScheme.primary,
    width: Dp = 40.dp,
    height: Dp = 4.dp,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentPage = minOf(tabPositions.lastIndex, pagerState.currentPage)
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffsetFraction

        val indicatorWidth = currentTab.width.toPx() * 1f

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
                indicatorOffset + ((currentTab.width - width).toPx() / 2),
                canvasHeight - height.toPx(),
            ),
            size = Size(width.toPx(), height.toPx()),
            cornerRadius = CornerRadius(50f),
        )
    }
}

@Composable
fun PagerTab(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    index: Int,
    pageCount: Int,
    text: String,
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedFontSize: TextUnit = 18.sp,
    unselectedFontSize: TextUnit = 15.sp,
    selectedFontWeight: FontWeight = FontWeight.Bold,
    unselectedFontWeight: FontWeight = FontWeight.Normal,
) {
    val previousIndex = maxOf(index - 1, 0)
    val nextIndex = minOf(index + 1, pageCount - 1)
    val currentIndexPlusOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction

    val progress =
        if (currentIndexPlusOffset >= previousIndex && currentIndexPlusOffset <= nextIndex) {
            1f - abs(index - currentIndexPlusOffset)
        } else {
            0f
        }

    val fontSize = lerp(unselectedFontSize, selectedFontSize, progress)
    val fontWeight = lerpFontWeight(unselectedFontWeight, selectedFontWeight, progress)
    val color = lerpColor(unselectedContentColor, selectedContentColor, progress)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = color, fontSize = fontSize, fontWeight = fontWeight)
    }
}
