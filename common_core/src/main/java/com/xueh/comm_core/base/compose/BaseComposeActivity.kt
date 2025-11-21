package com.xueh.comm_core.base.compose

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.LogUtils
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.ComposeMaterialTheme
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.weight.compose.ImageCompose
import com.xueh.comm_core.weight.compose.click

/**
 * 优化版 BaseComposeActivity
 *
 * 特性：
 * - 锁定竖屏（可按需修改）
 * - 统一使用 Scaffold 处理 topBar 和 content padding
 * - 只调用一次 enableEdgeToEdge() 初始化，然后在状态变化时更新样式
 * - 在 Compose 层将 fontScale 固定为 1.0（局部生效）
 */
abstract class BaseComposeActivity : ComponentActivity() {

    /** 状态栏图标颜色模式，true=白色图标（用于深色背景），false=黑色图标 */
    var isSystemBarLight by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 支持边缘到边缘（仅初始化一次）
        enableEdgeToEdge()

        // 锁定竖屏（如需去掉可 override 或删除）
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        super.onCreate(savedInstanceState)

        // 可由子类拦截返回；默认不拦截则 finish()
        onBackPressedDispatcher.addCallback {
            finish()
        }

        setContent {
            // 强制局部 fontScale = 1.0，避免系统字体放大破坏布局
            // 这只影响 Compose 层，不会修改系统 Resources。
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(baseDensity.density, 1.0f)
            ) {
                BaseComposeContent()
            }
        }
    }


    /**
     * 抽离出的 Compose 内容，保持 onCreate 中 setContent 简洁
     */
    @Composable
    private fun BaseComposeContent() {
        val isDark = AppThemeType.isDark(themeType = appThemeType)

        // 在 isDark / isSystemBarLight 变化时更新系统栏样式
        LaunchedEffect(isDark, isSystemBarLight) {
            updateSystemBarsStyle(isDark)
        }

        ComposeMaterialTheme {
            GrayAppAdapter(isGray = false) {
                Scaffold(
                    topBar = {
                        if (showTitleView()) {
                            getTitleView()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    // 内容区：Scaffold 会给出 innerPadding（包含topBar高度），内容区无需再加 safeBarPadding
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        setComposeContent()
                    }
                }
            }
        }
    }

    /**
     * 更新系统栏样式（只在 Compose 层触发）
     */
    private fun updateSystemBarsStyle(isDark: Boolean) {
        // 根据 isDark / isSystemBarLight 决定图标颜色（示例写法）
        enableEdgeToEdge(
            SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) {
                isDark || isSystemBarLight
            },
            SystemBarStyle.auto(android.graphics.Color.WHITE, android.graphics.Color.BLACK) {
                isDark || isSystemBarLight
            }
        )
    }

    /** 子类必须实现 Compose 页面主体内容 */
    @Composable
    abstract fun setComposeContent()

    /** 是否显示标题栏，默认显示（子类可重写） */
    protected open fun showTitleView() = true

    /** 页面标题（子类可重写） */
    protected open fun setTitle() = ""

    /** 是否显示返回图标（子类可重写） */
    protected open fun showBackIcon() = true

    /** 是否在布局中保留状态栏 padding（默认 true） */
    protected open fun showStatusBars() = true

    /** 标题栏视图：子类可重写以自定义 */
    @Composable
    protected open fun getTitleView() = CommonTitleView(
        title = setTitle(),
        showBackIcon = showBackIcon(),
        modifier = Modifier.safeBarPadding(showStatusBars()),
        backClick = { onBackPressedDispatcher.onBackPressed() }
    )

}

/**
 * 扩展：根据是否要显示状态栏 padding 来安全添加 statusBarsPadding
 */
fun Modifier.safeBarPadding(showStatus: Boolean): Modifier =
    if (showStatus) this.statusBarsPadding() else this

/**
 * 通用标题栏（可直接使用或在子类中 override）
 */
@Composable
fun CommonTitleView(
    title: String,
    @androidx.annotation.DrawableRes backIcon: Int = R.mipmap.bar_icon_back_black,
    modifier: Modifier = Modifier,
    titleBackgroundColor: Color = Color.White,
    showBackIcon: Boolean = true,
    rightContent: (@Composable () -> Unit)? = null,
    backClick: (() -> Unit)? = null,
    backIconSize: Dp = 28.dp,
    titleTextStyle: androidx.compose.ui.text.TextStyle = androidx.compose.material3.LocalTextStyle.current.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
) {
    val isDark = AppThemeType.isDark(themeType = appThemeType)
    val textColor = if (isDark) Color.White else Color.Black
    val backgroundColor = if (isDark) Color.Black else titleBackgroundColor

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .height(44.dp)
            .padding(horizontal = 8.dp)
    ) {
        if (showBackIcon) {
            // 使用你的 ImageCompose（支持 tint）
            ImageCompose(
                id = backIcon,
                modifier = Modifier
                    .size(backIconSize)
                    .click { backClick?.invoke() },
                colorFilter = if (isDark) ColorFilter.tint(Color.White) else null
            )
        } else {
            Spacer(modifier = Modifier.width(backIconSize))
        }

        // 标题文本：使用 weight + center 对齐
        Box(
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            Text(
                text = title,
                color = textColor,
                style = titleTextStyle,
                maxLines = 1
            )
        }

        // 右侧内容：自适应宽度并居中
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .height(44.dp),
            contentAlignment = Alignment.Center
        ) {
            rightContent?.invoke()
        }
    }
}
