package com.xueh.comm_core.web

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.IntentUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.MiddlewareWebChromeBase
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.base.compose.CommonTitleView
import com.xueh.comm_core.weight.compose.ImageCompose
import com.xueh.comm_core.weight.compose.click

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
        showShare = (uri.getQueryParameter("showShare") ?: "").isNotEmpty()

        if (hideTitle.isNotEmpty()) {
            BarUtils.transparentStatusBar(this)
            BarUtils.setStatusBarLightMode(this, false)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                AgentWeb()
                WebTitle(
                    title = titleStr,
                    alpha = alpha,
                    rightContent = { isDark ->
                        TitleRightShare(
                            colorFilter = if (isDark) ColorFilter.tint(Color.White) else null
                        )
                    },
                    back = {
                        if (agentWeb?.back() != true) {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                )
            }
        } else {
            BarUtils.setStatusBarLightMode(this, true)
            CommonTitleView(
                title = titleStr,
                titleBackgroundColor = Color.Transparent,
                rightContent = {
                    TitleRightShare()
                },
                backClick = {
                    if (agentWeb?.back() != true) {
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
            factory = { context ->
                LinearLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }.also { parent ->
                    agentWeb = AgentWeb.with(this@AgentComposeWebActivity)
                        .setAgentWebParent(
                            parent,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                        )
                        .useDefaultIndicator()
                        .interceptUnkownUrl()
                        .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                        .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                        .useMiddlewareWebChrome(object : MiddlewareWebChromeBase() {
                            override fun onReceivedTitle(view: WebView, title: String) {
                                super.onReceivedTitle(view, title)
                                if (!TextUtils.isEmpty(title) && titleStr.isEmpty()) {
                                    titleStr = truncateWebTitle(title)
                                }
                            }
                        })
                        .createAgentWeb()
                        .ready()
                        .go(getUrl())
                }
            },
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

    var showShare by mutableStateOf(false)

    @Composable
    fun TitleRightShare(colorFilter: ColorFilter? = null) {
        if (showShare) {
            ImageCompose(id = R.mipmap.ic_share,
                Modifier
                    .size(24.dp)
                    .click {
                        ActivityUtils.startActivity(
                            IntentUtils.getShareTextIntent(getUrl())
                        )
                    }, colorFilter = colorFilter
            )
        }
    }
}

