package com.xueh.comm_core.base.compose

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.ComposeMaterialTheme
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.weight.ImageCompose
import com.xueh.comm_core.weight.click

/**
 * 优化版 BaseComposeActivity
 *
 * 特性：
 * - 锁定竖屏（可按需修改）
 * - 完全沉浸式：状态栏和导航栏透明，内容绘制到系统栏后面
 * - 子类通过 immersiveStatusBar() 控制内容是否延伸到状态栏后面
 * - 子类通过 immersiveNavigationBar() 控制内容是否延伸到导航栏后面
 * - 基于设计稿宽度的 Density 等比缩放（今日头条适配方案）
 * - fontScale 固定为 1.0，屏蔽系统字体缩放
 *
 * 屏幕适配原理：
 * ┌─────────────────────────────────────────────────────────┐
 * │ 设计稿基准宽度 = 402dp（对齐 iPhone 16 Pro 的 402pt）     │
 * │                                                         │
 * │ 核心公式：scaledDensity = screenWidthPx / 402            │
 * │                                                         │
 * │ 效果：所有设备的逻辑宽度统一为 402dp，                     │
 * │       1dp 始终占屏幕宽度的 1/402，                        │
 * │       UI 元素在不同分辨率设备上保持一致的屏幕占比。         │
 * │                                                         │
 * │ 示例：                                                   │
 * │   Redmi K70  (1440px) → density = 1440/402 = 3.582      │
 * │   iQOO Neo5  (1080px) → density = 1080/402 = 2.687      │
 * │   两台设备上 47.dp 的日历格子占屏幕宽度比例完全一致        │
 * └─────────────────────────────────────────────────────────┘
 */
abstract class BaseComposeActivity : ComponentActivity() {

    companion object {
        /**
         * 设计稿基准宽度（dp），对齐 iOS 设计稿。
         *
         * iPhone 16 Pro 屏幕宽度为 402pt，iOS 的 1pt ≈ Android 的 1dp，
         * 因此以 402 作为基准，使 Android 端所有设备的逻辑宽度统一为 402dp，
         * 保证 UI 元素在不同分辨率设备上与 iOS 设计稿保持一致的屏幕占比。
         */
        private const val DESIGN_WIDTH_DP = 402f
    }

    /** 状态栏图标颜色模式，true=白色图标（用于深色背景），false=黑色图标 */
    var isSystemBarLight by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        super.onCreate(savedInstanceState)

        window.decorView.setBackgroundColor(android.graphics.Color.BLACK)

        onBackPressedDispatcher.addCallback {
            finish()
        }

        setContent {
            // ========== 屏幕适配：基于设计稿宽度的 Density 等比缩放 ==========
            //
            // 背景：
            //   Android 的 dp 保证「物理尺寸一致」而非「屏幕占比一致」。
            //   不同设备的 screenWidthDp 不同（如 K70=360dp, Neo5=393dp, S24=411dp），
            //   导致同样的 dp 值在不同设备上占屏幕的比例不一致。
            //
            // 方案（今日头条方案）：
            //   将 density 重新计算为 screenWidthPx / designWidthDp，
            //   使所有设备的逻辑宽度统一为设计稿宽度（402dp），
            //   dp 不再代表固定物理尺寸，而是代表「占屏幕宽度的固定比例」。
            //
            // 流程：
            //   1. 获取屏幕物理像素宽度 screenWidthPx
            //   2. 计算 scaledDensity = screenWidthPx / DESIGN_WIDTH_DP
            //   3. 通过 CompositionLocalProvider 覆盖 LocalDensity
            //   4. 所有 Compose 组件中的 dp/sp 值自动按新 density 转换为 px
            //
            val screenWidthPx = resources.displayMetrics.widthPixels.toFloat()
            val scaledDensity = screenWidthPx / DESIGN_WIDTH_DP

            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = scaledDensity,
                    fontScale = 1.0f
                )
            ) {
                BaseComposeContent()
            }
        }
    }

    @Composable
    private fun BaseComposeContent() {
        val isDark = AppThemeType.isDark(themeType = appThemeType)

        LaunchedEffect(isDark, isSystemBarLight) {
            updateSystemBarsStyle(isDark)
        }

        ComposeMaterialTheme {
            GrayAppAdapter(isGray = false) {
                val windowInsets = if (immersiveStatusBar() && immersiveNavigationBar()) {
                    WindowInsets(0)
                } else if (immersiveStatusBar()) {
                    WindowInsets.navigationBars
                } else if (immersiveNavigationBar()) {
                    WindowInsets.statusBars
                } else {
                    WindowInsets.systemBars
                }

                Scaffold(
                    topBar = {
                        if (showTitleView()) {
                            getTitleView()
                        }
                    },
                    containerColor = Color.Transparent,
                    contentWindowInsets = windowInsets,
                ) { innerPadding ->
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

    private fun updateSystemBarsStyle(isDark: Boolean) {
        val useLightIcons = isDark || isSystemBarLight
        if (useLightIcons) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            )
        } else {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                ),
                navigationBarStyle = SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            )
        }
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

    /** 内容是否延伸到状态栏后面（沉浸式），默认 false。为 true 时页面自行处理 statusBarsPadding */
    protected open fun immersiveStatusBar() = false

    /** 内容是否延伸到导航栏后面（沉浸式），默认 false。为 true 时页面自行处理 navigationBarsPadding */
    protected open fun immersiveNavigationBar() = false

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
