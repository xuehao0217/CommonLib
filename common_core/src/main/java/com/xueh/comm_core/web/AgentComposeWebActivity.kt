package com.xueh.comm_core.web

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.MiddlewareWebChromeBase
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.weight.compose.CommonTitleView

class AgentComposeWebActivity : BaseComposeActivity() {
    companion object {
        const val TITLE = "title"
        const val URL = "url"

        fun start(url: String, title: String = "") {
            ActivityUtils.startActivity(
                Intent(
                    ActivityUtils.getTopActivity(),
                    AgentComposeWebActivity::class.java
                ).apply {
                    putExtra(TITLE, title)
                    putExtra(URL, url)
                })
        }
    }

    var agentWeb: AgentWeb? = null
    var titleStr by mutableStateOf("")
    var alpha by mutableFloatStateOf(0f)

    override fun showTitleView() = false

    @Composable
    override fun setComposeContent() {
        val uri = Uri.parse(intent.getStringExtra(WebViewActivity.URL))
        val hideTitle = uri.getQueryParameter("hideTitle") ?: ""
        if (hideTitle.isNotEmpty()) {
            BarUtils.transparentStatusBar(this)
            BarUtils.setStatusBarLightMode(this, false)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                AgentWeb()
                WebTitle(titleStr, alpha) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        } else {
            BarUtils.setStatusBarLightMode(this, true)
            CommonTitleView(
                title = titleStr,
                titleBackgroundColor = Color.Transparent,
                backClick = {
                    if (agentWeb?.back() == true) {
                        agentWeb?.back()
                    } else {
                        onBackPressedDispatcher.onBackPressed()
                    }
                })
            AgentWeb()
        }
        agentWeb?.webCreator?.webView?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (hideTitle.isNotEmpty()) {
                alpha =
                    scrollY.toFloat() / (BarUtils.getStatusBarHeight() + ConvertUtils.dp2px(50f))
                BarUtils.setStatusBarLightMode(this, if (alpha >= 1) true else false)
            }
        }
    }

    @Composable
    fun AgentWeb(modifier: Modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = modifier,
            factory = {
                LinearLayout(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = {
                agentWeb = AgentWeb.with(this)
                    .setAgentWebParent(
                        it,
                        LinearLayout.LayoutParams(-1, -1)
                    )
                    .useDefaultIndicator()
                    .interceptUnkownUrl()
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .useMiddlewareWebChrome(object : MiddlewareWebChromeBase() {
                        override fun onReceivedTitle(view: WebView, title: String) {
                            super.onReceivedTitle(view, title)
                            if (!TextUtils.isEmpty(title) && titleStr.isEmpty()) {
                                if (title.length > 10) {
                                    titleStr = title.substring(0, 10) + "..."
                                }
                                titleStr = title
                            }
                        }
                    })
                    .createAgentWeb()
                    .ready()
                    .go(getUrl())

            }
        )
    }

    protected fun getUrl() = intent.getStringExtra(WebViewActivity.URL) ?: ""

    public override fun onPause() {
        agentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    public override fun onResume() {
        agentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (agentWeb?.handleKeyEvent(keyCode, event) == true) {
            true
        } else super.onKeyDown(keyCode, event)
    }


    @Preview
    @Composable
    fun WebTitle(title: String = "", alpha: Float = 0f, back: () -> Unit = {}) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
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
                    backClick = back
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .alpha(alpha - 1f)
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                CommonTitleView(
                    titleBackgroundColor = Color.White,
                    title = title,
                    backIcon = R.mipmap.bar_icon_back_black, backClick = back
                )
            }
        }
    }
}

