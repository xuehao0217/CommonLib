package com.xueh.comm_core.base.compose

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.ComposeMaterialTheme
import com.xueh.comm_core.base.compose.theme.PersistAppThemePreferencesEffect
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.widget.ImageCompose
import com.xueh.comm_core.widget.clickNoRipple

/**
 * 当前组合下的 [BaseComposeActivity]（若不在其树内则为 null）。
 * 用于减少 `LocalContext.current.findActivity()` 样板；勿用于生命周期关键路径。
 */
val LocalBaseComposeActivity: ProvidableCompositionLocal<BaseComposeActivity?> =
    staticCompositionLocalOf { null }

/**
 * 带边到边、设计稿宽度 Density、[ComposeMaterialTheme] 与可选默认标题栏的 Compose Activity 基类。
 *
 * **特性**
 * - [requestedScreenOrientation] 默认竖屏锁定，平板等可重写。
 * - 沉浸式：透明系统栏；[immersiveStatusBar] / [immersiveNavigationBar] 控制 [Scaffold] 是否消费对应 insets。
 * - 设计稿宽度等比缩放（今日头条方案）；`Density` 的 fontScale 固定 1.0；基准宽度见 [designWidthDp]。
 * - [mergeImeIntoContentWindowInsets] 为 true 时，内容区并入 IME，减轻键盘遮挡；重度表单仍建议在页面内 `verticalScroll` + `imePadding`。
 * - [isSecureWindow] 可开启防截屏（支付/证件等）。
 *
 * **状态栏相关 API 对照（易混）**
 *
 * | API | 作用 |
 * |-----|------|
 * | [showStatusBars] | 仅作用在默认标题栏：是否为标题行加 [statusBarsPadding]（与刘海下标题位置有关）。 |
 * | [immersiveStatusBar] | [Scaffold] 的 [androidx.compose.foundation.layout.WindowInsets] 是否包含状态栏：为 true 时内容可画到状态栏后，需页面自管 `statusBarsPadding`。 |
 * | [immersiveNavigationBar] | 同上，针对导航栏区域。 |
 *
 * **系统返回键**：[onCreate] 内会注册默认 `OnBackPressedCallback`（未消费时 [finish]）。后通过 [androidx.activity.OnBackPressedDispatcher.addCallback] 注册的回调会先执行；子类若在组合内注册自定义返回（如 [androidx.compose.runtime.DisposableEffect]），请勿依赖注册顺序以外的行为，并自行处理是否调用 [androidx.activity.OnBackPressedDispatcher.onBackPressed]。
 *
 * **刘海 / 横屏**：部分「只保留一侧系统栏 inset」的组合下，横屏刘海安全区可能仍需在具体页面补充 `displayCutout` / `safeDrawing`，见各沉浸式 API 说明。
 *
 * 屏幕适配：`scaledDensity = screenWidthPx / designWidthDp()`，使逻辑宽度对齐设计稿宽度，1dp 占屏宽固定比例。
 */
abstract class BaseComposeActivity : ComponentActivity() {

    /** 状态栏图标颜色模式，true=白色图标（用于深色背景），false=黑色图标 */
    var isSystemBarLight by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        requestedOrientation = requestedScreenOrientation()

        super.onCreate(savedInstanceState)

        if (isSecureWindow()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        window.decorView.setBackgroundColor(android.graphics.Color.BLACK)

        // 默认兜底：无子回调消费时结束 Activity。后注册的回调先执行，见类 KDoc。
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
            //   2. 计算 scaledDensity = screenWidthPx / designWidthDp()
            //   3. 通过 CompositionLocalProvider 覆盖 LocalDensity
            //   4. 所有 Compose 组件中的 dp/sp 值自动按新 density 转换为 px
            //
            // 依赖 LocalConfiguration，分屏/折叠/旋转后宽度变化会触发重组，避免仍用旧 widthPixels。
            val configuration = LocalConfiguration.current
            val screenWidthPx = remember(configuration.screenWidthDp, configuration.screenHeightDp) {
                resources.displayMetrics.widthPixels.toFloat()
            }
            val scaledDensity = screenWidthPx / this@BaseComposeActivity.designWidthDp()

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
        CompositionLocalProvider(LocalBaseComposeActivity provides this) {
            val isDark = AppThemeType.isDark(themeType = appThemeType)

            LaunchedEffect(isDark, isSystemBarLight) {
                updateSystemBarsStyle(isDark)
            }

            ComposeMaterialTheme {
                PersistAppThemePreferencesEffect()
                GrayAppAdapter(isGray = isAppGrayMode()) {
                    val systemBarInsets = when {
                        immersiveStatusBar() && immersiveNavigationBar() -> WindowInsets(0)
                        immersiveStatusBar() -> WindowInsets.navigationBars
                        immersiveNavigationBar() -> WindowInsets.statusBars
                        else -> WindowInsets.systemBars
                    }
                    val windowInsets =
                        if (mergeImeIntoContentWindowInsets()) {
                            systemBarInsets.union(WindowInsets.ime)
                        } else {
                            systemBarInsets
                        }

                    if (useActivityScaffold()) {
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
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(windowInsets)
                        ) {
                            setComposeContent()
                        }
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

    /**
     * 是否使用本类内置 [Scaffold]（顶栏 + [contentWindowInsets]）。
     * 为 false 时仅 [ComposeMaterialTheme] + 带 [windowInsetsPadding] 的容器，避免与页面内第二层 [Scaffold] 叠床架屋；标题与顶栏需自行实现。
     */
    protected open fun useActivityScaffold(): Boolean = true

    /**
     * 是否在 [contentWindowInsets] 中并入 [WindowInsets.ime]。
     * 全屏游戏/视频等可返回 false，避免键盘顶起全屏层。
     */
    protected open fun mergeImeIntoContentWindowInsets(): Boolean = true

    /** 全局灰度 UI（如活动志哀）；与 [GrayAppAdapter] 一致。 */
    protected open fun isAppGrayMode(): Boolean = false

    /**
     * 设计稿逻辑宽度基准（dp），用于 `scaledDensity = widthPx / designWidthDp()`。
     * 默认 402，对齐常见 iPhone 比例稿；特殊模块可重写。
     */
    protected open fun designWidthDp(): Float = 402f

    /** [android.app.Activity.setRequestedOrientation]；平板横屏宿主等可重写。 */
    protected open fun requestedScreenOrientation(): Int = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

    /** 为 true 时对窗口加 [WindowManager.LayoutParams.FLAG_SECURE]（防截屏/录屏，系统仍可能策略限制）。 */
    protected open fun isSecureWindow(): Boolean = false

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
    val backLabel = stringResource(R.string.comm_core_title_back)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .height(44.dp)
            .padding(horizontal = 8.dp)
    ) {
        if (showBackIcon) {
            ImageCompose(
                id = backIcon,
                modifier = Modifier
                    .size(backIconSize)
                    .semantics {
                        role = Role.Button
                        contentDescription = backLabel
                    }
                    .clickNoRipple { backClick?.invoke() },
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
                maxLines = 1,
                modifier = Modifier.then(
                    if (title.isNotEmpty()) {
                        Modifier.semantics { heading() }
                    } else {
                        Modifier
                    }
                )
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
