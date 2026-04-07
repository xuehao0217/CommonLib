package com.xueh.commonlib.ui

import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.web.AgentWebScaffold
import com.xueh.comm_core.widget.BottomNavPager
import com.xueh.comm_core.widget.NavData
import com.xueh.comm_core.widget.NavThemeColors
import com.xueh.commonlib.R
import kotlinx.coroutines.delay

/**
 * 应用唯一 Activity：启动闪屏 → 底部导航主界面；全屏 Web / 测试页以覆盖层呈现。
 */
class MainActivity : BaseComposeActivity() {
    companion object {
        var interceptTab by mutableStateOf(false)
        var showRedPoint = mutableStateOf(false)
    }
    enum class RootPhase { Splash, Main }

    private var rootPhase by mutableStateOf(RootPhase.Splash)
    private val overlayState = mutableStateOf<FullScreenOverlay>(FullScreenOverlay.None)
    private val shellController by lazy { AppShellController(overlayState) }

    override fun showTitleView() = false

    private var backPressedTime: Long = 0

    @Composable
    override fun setComposeContent() {
        val ctx = LocalContext.current
        val overlay by overlayState

        DisposableEffect(rootPhase, overlay) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (rootPhase == RootPhase.Splash) {
                        finish()
                        return
                    }
                    if (overlayState.value != FullScreenOverlay.None) {
                        // 先交给覆盖层内 BackHandler（AgentWeb 后退 / Test 页关闭）
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                        return
                    }
                    if (System.currentTimeMillis() - backPressedTime <= 2000) {
                        AppUtils.exitApp()
                    } else {
                        backPressedTime = System.currentTimeMillis()
                        ToastUtils.showShort("再按一次退出应用")
                    }
                }
            }
            onBackPressedDispatcher.addCallback(callback)
            onDispose { callback.remove() }
        }

        when (rootPhase) {
            RootPhase.Splash -> SplashRoute(onFinished = { rootPhase = RootPhase.Main })
            RootPhase.Main -> {
                CompositionLocalProvider(LocalAppShell provides shellController) {
                    Box(Modifier.fillMaxSize()) {
                        MainTabShell()

                        when (val layer = overlay) {
                            FullScreenOverlay.None -> { }
                            FullScreenOverlay.TestComposeDemo -> {
                                Box(Modifier.fillMaxSize()) {
                                    TestComposeDemoScreen()
                                }
                            }
                            is FullScreenOverlay.AgentWeb -> {
                                Box(Modifier.fillMaxSize()) {
                                    AgentWebScaffold(
                                        activity = ctx as androidx.activity.ComponentActivity,
                                        url = layer.url,
                                        initialTitle = layer.title,
                                        onClose = { shellController.dismissOverlay() },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainTabShell() {
    val scheme = MaterialTheme.colorScheme
    val pages = listOf<@Composable () -> Unit>(
        { HomePage() },
        { MinePage() },
        { TabPage3() },
    )

    val showRed by MainActivity.showRedPoint
    val navItems = remember(showRed) {
        listOf(
            NavData(
                selectIcon = R.mipmap.ic_home_select,
                unSelectIcon = R.mipmap.ic_home_normal,
                text = "首页",
            ),
            NavData(
                selectIcon = R.mipmap.ic_my_select,
                unSelectIcon = R.mipmap.ic_my_normal,
                text = "我的",
                showRed = showRed,
            ),
            NavData(
                selectIcon = R.mipmap.ic_my_select,
                unSelectIcon = R.mipmap.ic_my_normal,
                text = "拦截",
            ),
        )
    }

    LaunchedEffect(Unit) {
        delay(10_000)
        MainActivity.showRedPoint.value = true
    }

    BottomNavPager(
        pages = pages,
        navItems = navItems,
        fontSize = 12.sp,
        themeColors = NavThemeColors(
            lightBackground = scheme.surfaceContainerLow,
            darkBackground = scheme.surfaceContainerLow,
            lightUnSelectedTextColor = scheme.onSurfaceVariant,
            darkUnSelectedTextColor = scheme.onSurfaceVariant,
            lightSelectedTextColor = scheme.primary,
            darkSelectedTextColor = scheme.primary,
        ),
        interceptClick = { index ->
            MainActivity.interceptTab && index == 2
        },
    )
}
