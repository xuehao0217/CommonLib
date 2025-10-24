package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// ---------------------- 数据模型 ----------------------
data class NavData(
    val selectIcon: Int,
    val unSelectIcon: Int,
    val text: String,
    val showRed: MutableState<Boolean> = mutableStateOf(false),
)

// ---------------------- BottomNavPager ----------------------
@Composable
fun BottomNavPager(
    pages: List<@Composable () -> Unit>,
    navItems: List<NavData>,
    modifier: Modifier = Modifier,
    selectTextColor: Color = MaterialTheme.colorScheme.primary,
    unSelectTextColor: Color = Color.Gray,
    fontSize: TextUnit = TextUnit.Unspecified,
    imageSize: Dp = 24.dp,
    userScrollEnabled: Boolean = false, // 是否支持滑动
    interceptClick: ((Int) -> Boolean)? = null,
) {
    val pagerState = rememberPagerState { pages.size }
    val selectedIndex by remember { derivedStateOf { pagerState.currentPage } }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        // ----------------- Pager -----------------
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled=userScrollEnabled,
        ) { page ->
            pages[page]()
        }

        // ----------------- Bottom Navigation -----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEachIndexed { index, nav ->
                NavItem(
                    navData = nav,
                    selected = index == selectedIndex,
                    selectTextColor = selectTextColor,
                    unSelectTextColor = unSelectTextColor,
                    imageSize = imageSize,
                    fontSize = fontSize,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickDebounce {
                            val intercepted = interceptClick?.invoke(index) ?: false
                            if (!intercepted) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        }
                )
            }
        }
    }
}

// ---------------------- 单个 NavItem ----------------------
@Composable
fun NavItem(
    navData: NavData,
    selected: Boolean,
    selectTextColor: Color,
    unSelectTextColor: Color,
    imageSize: Dp,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(NavData, Boolean) -> Unit = { nav, isSelected ->
        // 默认内容布局
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // 这里使用 ImageCompose 或 Image 加载图标
                ImageCompose(
                    id = if (isSelected) nav.selectIcon else nav.unSelectIcon,
                    modifier = Modifier.size(imageSize)
                )

                if (nav.showRed.value) {
                    Canvas(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        drawCircle(Color.Red, radius = size.minDimension / 2)
                    }
                }
            }

            Text(
                text = nav.text,
                fontSize = fontSize,
                color = if (isSelected) selectTextColor else unSelectTextColor
            )
        }
    },
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        content(navData, selected)
    }
}
