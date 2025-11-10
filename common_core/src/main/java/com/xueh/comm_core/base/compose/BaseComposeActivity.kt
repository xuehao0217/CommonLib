package com.xueh.comm_core.base.compose

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
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
 * Base Compose Activity
 * 1. 自动锁定竖屏
 * 2. 支持状态栏颜色自适应
 * 3. 可选择显示标题栏
 * 4. 支持灰度模式适配
 */
abstract class BaseComposeActivity : ComponentActivity() {

    /** 状态栏图标颜色模式，true 白色图标，false 黑色图标 */
    var isSystemBarLight by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 支持边缘到边缘
        enableEdgeToEdge()
        // 锁定竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
        super.onCreate(savedInstanceState)

        // 默认返回事件处理
        onBackPressedDispatcher.addCallback { finish() }

        setContent {
            val isDark = AppThemeType.isDark(themeType = appThemeType)

            // 设置状态栏样式，只在组合内容首次加载或状态变化时调用
            LaunchedEffect(isDark, isSystemBarLight) {
                setSystemBarsStyle(isDark)
            }

            // Compose内容Modifier
            val contentModifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .then(if (showStatusBars()) Modifier.statusBarsPadding() else Modifier)

            ComposeMaterialTheme {
                GrayAppAdapter(isGray = false) {
                    Column(modifier = contentModifier) {
                        if (showTitleView()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .then(if (showStatusBars()) Modifier.statusBarsPadding() else Modifier)
                            ) {
                                getTitleView()
                            }
                        }
                        // 页面实际内容由子类实现
                        setComposeContent()
                    }
                }
            }
        }
    }

    /**
     * 设置状态栏样式
     */
    private fun setSystemBarsStyle(isDark: Boolean) {
        enableEdgeToEdge(
            SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) {
                isDark || isSystemBarLight
            },
            SystemBarStyle.auto(android.graphics.Color.WHITE, android.graphics.Color.BLACK) {
                isDark || isSystemBarLight
            }
        )
    }

    /** 子类必须实现Compose页面内容 */
    @Composable
    abstract fun setComposeContent()

    /** 是否显示标题栏，默认显示 */
    protected open fun showTitleView() = true

    /** 页面标题，默认空 */
    protected open fun setTitle() = ""

    /** 是否显示返回按钮，默认显示 */
    protected open fun showBackIcon() = true

    /** 是否显示状态栏padding，默认显示 */
    protected open fun showStatusBars() = true

    /** 标题栏View，可自定义 */
    @Composable
    protected open fun getTitleView() = CommonTitleView(
        title = setTitle(),
        showBackIcon = showBackIcon(),
        backClick = { onBackPressedDispatcher.onBackPressed() }
    )

    /**
     * 保持字体缩放固定为1.0
     * 避免系统字体放大影响UI布局
     */
    override fun getResources(): Resources {
        val resources = super.getResources()
        val configuration = resources.configuration
        if (configuration.fontScale != 1.0f) {
            LogUtils.e("Reset fontScale to 1.0: ${configuration.fontScale}")
            configuration.fontScale = 1.0f
            return createConfigurationContext(configuration).resources
        }
        return resources
    }
}

/**
 * 公共标题栏
 */
@Composable
fun CommonTitleView(
    title: String,
    @DrawableRes backIcon: Int = R.mipmap.bar_icon_back_black,
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
    ) {
        // 左侧返回按钮
        if (showBackIcon) {
            ImageCompose(
                id = backIcon,
                modifier = Modifier.size(backIconSize).click { backClick?.invoke() },
                colorFilter = if (isDark) ColorFilter.tint(Color.White) else null
            )
        } else Spacer(modifier = Modifier.width(backIconSize))

        // 标题文本
        Text(
            text = title,
            color = textColor,
            style = titleTextStyle,
            maxLines = 1,
            modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally)
        )

        // 右侧内容
        Box(modifier = Modifier.width(backIconSize)) {
            rightContent?.invoke()
        }
    }
}
