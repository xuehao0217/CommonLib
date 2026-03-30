/**
 * 底部导航与横向分页：[NavData]、[BottomNavPager]；点击 Tab 与 [HorizontalPager] 页码双向同步，支持红点 [NavData.showRed]。
 */
package com.xueh.comm_core.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.compose.theme.isThemeDark
import kotlinx.coroutines.launch

// ---------------------- 数据模型 ----------------------

/**
 * 底部导航项数据模型。
 *
 * @param selectIcon 选中状态下的图标资源 ID
 * @param unSelectIcon 未选中状态下的图标资源 ID
 * @param text 导航项显示文本
 * @param showRed 是否显示红点角标；状态由上层持有并在变更时传入新 [NavData] 或刷新列表。
 */
data class NavData(
    val selectIcon: Int,
    val unSelectIcon: Int,
    val text: String,
    val showRed: Boolean = false,
)

/**
 * 底部导航栏主题配色，支持日夜间模式。
 *
 * @param lightBackground 浅色模式背景色
 * @param darkBackground 深色模式背景色
 * @param lightUnSelectedTextColor 浅色模式未选中文本颜色
 * @param darkUnSelectedTextColor 深色模式未选中文本颜色
 * @param lightSelectedTextColor 浅色模式选中文本颜色
 * @param darkSelectedTextColor 深色模式选中文本颜色
 */
data class NavThemeColors(
    val lightBackground: Color = Color(0xFFFFFFFF),           // 白色背景
    val darkBackground: Color = Color(0xFF121212),            // Material Dark背景

    val lightUnSelectedTextColor: Color = Color(0xFF757575),  // 轻灰色，未选中
    val darkUnSelectedTextColor: Color = Color(0xFFAAAAAA),   // 深灰色，未选中

    val lightSelectedTextColor: Color = Color(0xFF1E88E5),    // 蓝色，突出选中
    val darkSelectedTextColor: Color = Color(0xFF90CAF9),     // 浅蓝色，夜间选中
)


// ---------------------- BottomNavPager ----------------------

/**
 * 底部导航 + 横向分页布局组合。
 *
 * 导航流程：
 * 1. [HorizontalPager] 承载各页面内容
 * 2. 底部 Row 渲染导航项
 * 3. 点击 Tab 时调用 [animateScrollToPage] 切换页面
 * 4. [interceptClick] 可拦截点击，返回 true 时阻止导航切换
 *
 * @param pages 各页面对应的 Composable 内容，与 [navItems] **一一对应且长度必须相同**
 * @param navItems 导航项数据列表
 * @param interceptClick 点击拦截回调，返回 true 时阻止导航
 */
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
    require(pages.size == navItems.size) {
        "BottomNavPager: pages.size (${pages.size}) must equal navItems.size (${navItems.size})."
    }

    val pagerState = rememberPagerState { pages.size }
    val selectedIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    val isDark = isThemeDark()
    val barBackground = if (isDark) themeColors.darkBackground else themeColors.lightBackground
    val selectColor = if (isDark) themeColors.darkSelectedTextColor else themeColors.lightSelectedTextColor
    val unselectColor = if (isDark) themeColors.darkUnSelectedTextColor else themeColors.lightUnSelectedTextColor

    Column(modifier = modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = userScrollEnabled,
        ) { page ->
            pages[page]()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(barBackground),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEachIndexed { index, nav ->
                val selected = index == selectedIndex
                NavItem(
                    navData = nav,
                    selected = selected,
                    selectTextColor = selectColor,
                    unSelectTextColor = unselectColor,
                    imageSize = imageSize,
                    fontSize = fontSize,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .semantics {
                            role = Role.Tab
                            this.selected = selected
                        }
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

/**
 * 单个底部导航项，展示图标、文本及可选红点角标。
 *
 * @param navData 导航项数据
 * @param selected 是否处于选中状态
 * @param content 自定义内容插槽；为 null 时使用内置图标+文本+红点布局（更易保持组合稳定性）
 */
@Composable
fun NavItem(
    navData: NavData,
    selected: Boolean,
    selectTextColor: Color,
    unSelectTextColor: Color,
    imageSize: Dp,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    content: (@Composable BoxScope.(NavData, Boolean) -> Unit)? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (content != null) {
            content(navData, selected)
        } else {
            DefaultNavItemContent(
                navData = navData,
                selected = selected,
                selectTextColor = selectTextColor,
                unSelectTextColor = unSelectTextColor,
                imageSize = imageSize,
                fontSize = fontSize,
            )
        }
    }
}

@Composable
private fun DefaultNavItemContent(
    navData: NavData,
    selected: Boolean,
    selectTextColor: Color,
    unSelectTextColor: Color,
    imageSize: Dp,
    fontSize: TextUnit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            ImageCompose(
                id = if (selected) navData.selectIcon else navData.unSelectIcon,
                modifier = Modifier.size(imageSize),
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (selected) selectTextColor else unSelectTextColor)
            )

            if (navData.showRed) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(6.dp)
                        .background(Color.Red, CircleShape)
                )
            }
        }

        Text(
            text = navData.text,
            fontSize = fontSize,
            color = if (selected) selectTextColor else unSelectTextColor
        )
    }
}
