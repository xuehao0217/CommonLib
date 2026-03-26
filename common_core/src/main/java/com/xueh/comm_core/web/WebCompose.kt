/**
 * 使用 **AndroidView** 嵌入系统 [WebView] 的页面与组件。
 *
 * **流程**：[CustomWebView] 设置 [WebViewClient] / [WebChromeClient] → 进度回调驱动顶部进度条 → [BackHandler] 处理返回栈；
 * 非 http(s) 链接触发外部 [Intent]。[WebTitle] 为双栏透明/实底标题栏，随滚动 [alpha] 过渡。
 *
 * 参考：[gist 示例](https://gist.github.com/TheMelody/6ec3fcba6d57f544465651acc480ff39)
 */
package com.xueh.comm_core.web

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.blankj.utilcode.util.LogUtils
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.CommonTitleView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter

@Preview
@Composable
fun WebViewPage(
    url: String = "",
    webViewExt: ((webView: WebView?) -> Unit)? = null,
    onBack: () -> Unit = {},
) {
    LogUtils.iTag("WebViewPage", "url==${url}")
    var rememberWebViewProgress: Int by remember { mutableStateOf(-1) }
    Box {
        CustomWebView(
            modifier = Modifier.fillMaxSize(),
            url = url,
            onProgressChange = { progress ->
                rememberWebViewProgress = progress
            },
            initSettings = { settings ->
                settings?.apply {
                    //支持js交互
                    javaScriptEnabled = true
                    //将图片调整到适合webView的大小
                    useWideViewPort = true
                    //缩放至屏幕的大小
                    loadWithOverviewMode = true
                    //缩放操作
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = true
                    //是否支持通过JS打开新窗口
                    javaScriptCanOpenWindowsAutomatically = true
                    //不加载缓存内容
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }
            }, onBack = {
                onBack()
            }, onReceivedError = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                }
            }
        )
        LinearProgressIndicator(
            progress = { rememberWebViewProgress * 1.0F / 100F },
            modifier = Modifier
                .fillMaxWidth()
                .height(if (rememberWebViewProgress == 100) 0.dp else 5.dp),
            color = Color(0xFFF87D39)
        )
    }
}

@Composable
fun CustomWebView(
    modifier: Modifier = Modifier,
    url: String,
    onBack: () -> Unit,
    onProgressChange: (progress: Int) -> Unit = {},
    initSettings: (webSettings: WebSettings?) -> Unit = {},
    onReceivedError: (error: WebResourceError?) -> Unit = {}
) {
    val webViewChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            //回调网页内容加载进度
            onProgressChange(newProgress)
            super.onProgressChanged(view, newProgress)
        }
    }
    val webViewClient = object : WebViewClient() {
        override fun onPageStarted(
            view: WebView?, url: String?,
            favicon: Bitmap?
        ) {
            super.onPageStarted(view, url, favicon)
            onProgressChange(-1)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onProgressChange(100)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (null == request?.url) return false
            val showOverrideUrl = request.url.toString()
            try {
                if (!showOverrideUrl.startsWith("http://")
                    && !showOverrideUrl.startsWith("https://")
                ) {
                    //处理非http和https开头的链接地址
                    Intent(Intent.ACTION_VIEW, Uri.parse(showOverrideUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        view?.context?.applicationContext?.startActivity(this)
                    }
                    return true
                }
            } catch (e: Exception) {
                //没有安装和找到能打开(「xxxx://openlink.cc....」、「weixin://xxxxx」等)协议的应用
                return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            //自行处理....
            onReceivedError(error)
        }
    }
    var webView: WebView? = null
    AndroidView(modifier = modifier, factory = { ctx ->
        WebView(ctx).apply {
            this.webViewClient = webViewClient
            this.webChromeClient = webViewChromeClient
            initSettings(this.settings)
            webView = this
            loadUrl(url)
        }
    })
    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBack.invoke()
        }
    }
}

/**
 * 通用 Web 页标题栏（支持滚动渐变效果）
 *
 * 通过 alpha 值控制两层标题栏的显隐切换：
 * - alpha 接近 0 时，显示透明底 + 白色返回图标
 * - alpha 接近 1 时，显示白色底 + 黑色返回图标 + 标题文字
 *
 * @param title 标题文字
 * @param alpha 透明度（0~1），通常由页面滚动距离计算得出
 * @param rightContent 右侧可选内容（如分享按钮）
 * @param back 返回按钮点击回调
 */
@Composable
fun WebTitle(
    title: String = "",
    alpha: Float = 0f,
    rightContent: (@Composable (isDark: Boolean) -> Unit)? = null,
    back: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .alpha(1f - alpha)
                .background(Color.Transparent)
                .statusBarsPadding()
        ) {
            CommonTitleView(
                titleBackgroundColor = Color.Transparent,
                title = "",
                backIcon = R.mipmap.bar_icon_back_white,
                backClick = back,
                rightContent = { rightContent?.invoke(true) }
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .alpha(alpha)
                .background(Color.White)
                .statusBarsPadding()
        ) {
            CommonTitleView(
                titleBackgroundColor = Color.White,
                title = title,
                backIcon = R.mipmap.bar_icon_back_black,
                backClick = back,
                rightContent = { rightContent?.invoke(false) }
            )
        }
    }
}

/**
 * 处理 WebView 接收到的标题，超过 [maxLength] 时截断并添加省略号。
 *
 * @param title 原始标题
 * @param maxLength 最大显示长度，默认 10
 * @return 处理后的标题
 */
fun truncateWebTitle(title: String, maxLength: Int = 10): String =
    if (title.length > maxLength) title.substring(0, maxLength) + "..." else title
