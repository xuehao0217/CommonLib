package com.xueh.comm_core.web

import android.net.Uri
import android.text.TextUtils
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.IntentUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.MiddlewareWebChromeBase
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.CommonTitleView
import com.xueh.comm_core.widget.ImageCompose
import com.xueh.comm_core.widget.clickNoRipple

/**
 * **AgentWeb** 全屏内容：标题栏、分享、URL 查询参数（如 `hideTitle`）、与系统栏适配；供单 Activity 内嵌或与 [AgentComposeWebActivity] 复用。
 *
 * **生命周期流程**：[AndroidView] 创建 AgentWeb → [DisposableEffect] 绑定 [Lifecycle] `ON_PAUSE`/`ON_RESUME` → `onDispose` 时 [AgentWeb.webLifeCycle.onDestroy]。
 * [BackHandler]：优先 `agentWeb.back()`，否则 [onClose]。
 */
@Composable
fun AgentWebScaffold(
    activity: ComponentActivity,
    url: String,
    initialTitle: String = "",
    onClose: () -> Unit,
) {
    val uri = remember(url) { runCatching { Uri.parse(url) }.getOrNull() }
    val hideTitle = uri?.getQueryParameter("hideTitle") ?: ""
    val showShare = (uri?.getQueryParameter("showShare") ?: "").isNotEmpty()

    val titleState = remember(url, initialTitle) { mutableStateOf(initialTitle) }
    var alpha by remember { mutableFloatStateOf(0f) }
    var agentWeb by remember { mutableStateOf<AgentWeb?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, agentWeb) {
        val web = agentWeb
        if (web == null) {
            return@DisposableEffect onDispose { }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> web.webLifeCycle.onPause()
                Lifecycle.Event.ON_RESUME -> web.webLifeCycle.onResume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            agentWeb?.webLifeCycle?.onDestroy()
            agentWeb = null
        }
    }

    BackHandler {
        if (agentWeb?.back() != true) {
            onClose()
        }
    }

    LaunchedEffect(agentWeb, hideTitle, activity) {
        val web = agentWeb ?: return@LaunchedEffect
        val wv = web.webCreator?.webView
        if (hideTitle.isNotEmpty()) {
            wv?.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                alpha =
                    scrollY.toFloat() / (BarUtils.getStatusBarHeight() + ConvertUtils.dp2px(50f))
                BarUtils.setStatusBarLightMode(activity, alpha >= 1f)
            }
        } else {
            wv?.setOnScrollChangeListener(null)
        }
    }

    if (hideTitle.isNotEmpty()) {
        BarUtils.transparentStatusBar(activity)
        BarUtils.setStatusBarLightMode(activity, false)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            AgentWebAndroidView(
                activity = activity,
                url = url,
                titleState = titleState,
                onAgentWebCreated = { agentWeb = it },
            )
            WebTitle(
                title = titleState.value,
                alpha = alpha,
                rightContent = { isDark ->
                    AgentWebShareAction(
                        showShare = showShare,
                        shareUrl = url,
                        colorFilter = if (isDark) ColorFilter.tint(Color.White) else null,
                    )
                },
                back = {
                    if (agentWeb?.back() != true) {
                        onClose()
                    }
                },
            )
        }
    } else {
        BarUtils.setStatusBarLightMode(activity, true)
        Column(Modifier.fillMaxSize()) {
            CommonTitleView(
                title = titleState.value,
                titleBackgroundColor = Color.Transparent,
                rightContent = {
                    AgentWebShareAction(showShare = showShare, shareUrl = url)
                },
                backClick = {
                    if (agentWeb?.back() != true) {
                        onClose()
                    }
                },
            )
            AgentWebAndroidView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                activity = activity,
                url = url,
                titleState = titleState,
                onAgentWebCreated = { agentWeb = it },
            )
        }
    }
}

@Composable
private fun AgentWebAndroidView(
    activity: ComponentActivity,
    url: String,
    titleState: MutableState<String>,
    onAgentWebCreated: (AgentWeb) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                val aw = AgentWeb.with(activity)
                    .setAgentWebParent(
                        this,
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                        ),
                    )
                    .useDefaultIndicator()
                    .interceptUnkownUrl()
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .useMiddlewareWebChrome(object : MiddlewareWebChromeBase() {
                        override fun onReceivedTitle(view: WebView, title: String) {
                            super.onReceivedTitle(view, title)
                            if (!TextUtils.isEmpty(title) && titleState.value.isEmpty()) {
                                titleState.value = truncateWebTitle(title)
                            }
                        }
                    })
                    .createAgentWeb()
                    .ready()
                    .go(url)
                post { onAgentWebCreated(aw) }
            }
        },
    )
}

@Composable
private fun AgentWebShareAction(
    showShare: Boolean,
    shareUrl: String,
    colorFilter: ColorFilter? = null,
) {
    if (showShare) {
        ImageCompose(
            id = R.mipmap.ic_share,
            Modifier
                .size(24.dp)
                .clickNoRipple {
                    ActivityUtils.getTopActivity()?.startActivity(
                        IntentUtils.getShareTextIntent(shareUrl),
                    )
                },
            colorFilter = colorFilter,
        )
    }
}
