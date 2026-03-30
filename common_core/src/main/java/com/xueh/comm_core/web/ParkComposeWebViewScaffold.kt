/**
 * 对 [parkwoocheol/compose-webview](https://github.com/parkwoocheol/compose-webview)（`io.github.parkwoocheol:compose-webview-android`）的薄封装入口。
 *
 * 典型场景：在 Compose 中嵌入系统 WebView，需要 **配置变更后保留导航/滚动等状态** 时，用本组件比自行 `AndroidView` 更省事；
 * 若需 **AgentWeb 生命周期、分享、URL 查询参数标题栏** 等，请改用 [AgentWebScaffold]。
 *
 * ## 用法示例
 *
 * ```
 * @Composable
 * fun WebScreen() {
 *     ParkComposeWebViewScaffold(
 *         url = "https://example.com",
 *         modifier = Modifier.fillMaxSize(),
 *         showTopProgress = true,
 *         onWebViewCreated = { wv ->
 *             wv.settings.javaScriptEnabled = true
 *             // 在默认设置之上追加业务配置
 *         },
 *     )
 * }
 * ```
 *
 * **切换 URL**：本实现用 [androidx.compose.runtime.key] 包裹内容，`url` 变化会丢弃旧 [com.parkwoocheol.composewebview.WebViewState] 并重新加载；
 * 若要在同一会话内多次 `loadUrl` 且保留历史栈，请直接使用 `com.parkwoocheol.composewebview` 的
 * [com.parkwoocheol.composewebview.ComposeWebView] + [com.parkwoocheol.composewebview.WebViewController] 自行组合。
 */
package com.xueh.comm_core.web

import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.parkwoocheol.composewebview.ComposeWebView
import com.parkwoocheol.composewebview.LoadingState
import com.parkwoocheol.composewebview.rememberSaveableWebViewState
import com.parkwoocheol.composewebview.rememberWebViewController

/**
 * 在 [onWebViewCreated] 默认回调之前应用的基线配置；业务可在 lambda 中继续修改 [WebView.settings]。
 *
 * 当前默认：开启 JavaScript、DOM 存储、`loadWithOverviewMode`、`useWideViewPort`。
 */
private fun applyDefaultParkWebSettings(webView: WebView) {
    webView.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
    }
}

/**
 * 使用 [rememberSaveableWebViewState] 与 [rememberWebViewController] 驱动上游 [ComposeWebView]，
 * 可选在顶部展示 [LoadingState.Loading] 对应的 [LinearProgressIndicator]。
 *
 * @param url 首次加载地址（http/https 等）。变化时整段组合会随 [key] 重建，相当于新开一页；空字符串行为取决于 WebView/上游库，请传入合法 URL。
 * @param modifier 作用于外层 [Column]（含进度条与 Web 区域的整体布局）；内部 Web 区域在 Column 内使用 [Modifier.weight] 占满剩余高度。
 * @param showTopProgress 为 `true` 时，当 [com.parkwoocheol.composewebview.WebViewState.loadingState] 为 [LoadingState.Loading] 时在顶部绘制橙色线性进度条；为 `false` 则完全由页面内容表现加载态。
 * @param onWebViewCreated 传给上游 [ComposeWebView] 的 `onCreated`，在系统 `WebView` 实例创建后调用。**使用默认值**时仅执行 [applyDefaultParkWebSettings]（JS、DOM 存储、overview 与 wide viewport）。**若传入自定义 lambda**，则完全替换该默认行为，需自行设置 [WebView.settings]（可与默认行为对齐：开启 `javaScriptEnabled`、`domStorageEnabled`、`loadWithOverviewMode`、`useWideViewPort` 等）。
 *
 * @see com.parkwoocheol.composewebview.ComposeWebView 上游主组件，可传入自定义 client、jsBridge、error UI 等。
 * @see AgentWebScaffold 另一条 Web 技术栈（AgentWeb）。
 */
@Composable
fun ParkComposeWebViewScaffold(
    url: String,
    modifier: Modifier = Modifier,
    showTopProgress: Boolean = true,
    onWebViewCreated: (WebView) -> Unit = { applyDefaultParkWebSettings(it) },
) {
    key(url) {
        val state = rememberSaveableWebViewState(url = url)
        val controller = rememberWebViewController()
        Column(modifier.fillMaxSize()) {
            if (showTopProgress) {
                val loadingState = state.loadingState
                if (loadingState is LoadingState.Loading) {
                    LinearProgressIndicator(
                        progress = { loadingState.progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF87D39),
                    )
                }
            }
            ComposeWebView(
                state = state,
                controller = controller,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onCreated = onWebViewCreated,
            )
        }
    }
}
