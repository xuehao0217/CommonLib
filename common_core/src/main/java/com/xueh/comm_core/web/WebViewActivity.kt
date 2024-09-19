package com.xueh.comm_core.web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils.dp2px
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.MiddlewareWebChromeBase
import com.xueh.comm_core.R
import com.xueh.comm_core.base.xml.BaseActivity
import com.xueh.comm_core.databinding.ActivityWebViewBinding
import com.xueh.comm_core.weight.compose.CommonTitleView


/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/11 11:16
 * 备注：一个只用于显示的web页面
 */
open class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {

    companion object {
        const val TITLE = "title"
        const val URL = "url"

        fun start(url: String, title: String = "") {
            ActivityUtils.startActivity(
                Intent(
                    ActivityUtils.getTopActivity(),
                    WebViewActivity::class.java
                ).apply {
                    putExtra(TITLE, title)
                    putExtra(URL, url)
                })
        }
    }

    protected fun getUrl() = intent.getStringExtra(URL) ?: ""

    lateinit var agentWeb: AgentWeb

    public override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun initData() {

    }

    public override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (agentWeb.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }


    var titleStr by mutableStateOf("")

    var alpha by mutableStateOf(0f)
    override fun initView(savedInstanceState: Bundle?) {
        titleStr = intent?.getStringExtra(TITLE) ?: ""

        val uri = Uri.parse(intent.getStringExtra(URL))
        val hideTitle = uri.getQueryParameter("hideTitle") ?: ""


        if (hideTitle.isNotEmpty()) {
            BarUtils.transparentStatusBar(this)
            binding.composeViewTitle.setContent {
                WebTitle(titleStr, alpha) {
                    this.finish()
                }
            }
            binding.tbTitleBar.visibility = View.GONE
            binding.composeViewTitle.visibility = View.VISIBLE
        } else {
            BarUtils.addMarginTopEqualStatusBarHeight(binding.tbTitleBar)
            binding.composeViewTitle.visibility = View.GONE
            binding.tbTitleBar.visibility = View.VISIBLE
            binding.tbTitleBar.setContent {
                CommonTitleView(
                    title = titleStr,
                    titleBackgroundColor = Color.Transparent,
                    backClick = {
                        finish()
                    })
            }
        }
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.linerView, LinearLayout.LayoutParams(-1, -1))
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


        agentWeb.webCreator.webView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            alpha = scrollY.toFloat() / (BarUtils.getStatusBarHeight() + dp2px(50f))
        }
    }

    override fun initListener() {

    }

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
                .background(Color.Transparent).statusBarsPadding()
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