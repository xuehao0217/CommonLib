package com.xueh.comm_core.weight.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.compose.theme.isThemeDark
import kotlinx.coroutines.launch

// ---------------------- 数据模型 ----------------------
data class NavData(
    val selectIcon: Int,
    val unSelectIcon: Int,
    val text: String,
    val showRed: MutableState<Boolean> = mutableStateOf(false),
)

data class NavThemeColors(
    val lightBackground: Color = Color(0xFFFFFFFF),           // 白色背景
    val darkBackground: Color = Color(0xFF121212),            // Material Dark背景

    val lightUnSelectedTextColor: Color = Color(0xFF757575),  // 轻灰色，未选中
    val darkUnSelectedTextColor: Color = Color(0xFFAAAAAA),   // 深灰色，未选中

    val lightSelectedTextColor: Color = Color(0xFF1E88E5),    // 蓝色，突出选中
    val darkSelectedTextColor: Color = Color(0xFF90CAF9),     // 浅蓝色，夜间选中
)


// ---------------------- BottomNavPager ----------------------
@Composable
fun BottomNavPager(
    pages: List<@Composable () -> Unit>,
    navItems: List<NavData>,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    themeColors: NavThemeColors = NavThemeColors(),
    imageSize: Dp = 24.dp,
    userScrollEnabled: Boolean = false,
    interceptClick: ((Int) -> Boolean)? = null,
) {
    val pagerState = rememberPagerState { pages.size }
    val selectedIndex by remember { derivedStateOf { pagerState.currentPage } }
    val coroutineScope = rememberCoroutineScope()

    // 动态日夜间状态
    val isDark = isThemeDark()

    Column(modifier = modifier.fillMaxSize()) {

        // ----------------- Pager -----------------
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = userScrollEnabled,
        ) { page ->
            pages[page]()
        }

        // ----------------- Bottom Navigation -----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(if (isDark) themeColors.darkBackground else themeColors.lightBackground),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEachIndexed { index, nav ->
                NavItem(
                    navData = nav,
                    selected = index == selectedIndex,
                    selectTextColor =  if (isDark) themeColors.darkSelectedTextColor else themeColors.lightSelectedTextColor,
                    unSelectTextColor = if (isDark) themeColors.darkUnSelectedTextColor else themeColors.lightUnSelectedTextColor,
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // 图标
                ImageCompose(
                    id = if (isSelected) nav.selectIcon else nav.unSelectIcon,
                    modifier = Modifier.size(imageSize),
                    colorFilter = ColorFilter.tint(if (selected) selectTextColor else unSelectTextColor)
                )

                // 红点
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
    }
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        content(navData, selected)
    }
}
