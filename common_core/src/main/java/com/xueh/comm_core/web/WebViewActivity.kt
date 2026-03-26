package com.xueh.comm_core.web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils.dp2px
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.MiddlewareWebChromeBase
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.CommonTitleView
import com.xueh.comm_core.base.xml.BaseActivity
import com.xueh.comm_core.databinding.ActivityWebViewBinding


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
            BarUtils.setStatusBarLightMode(this,false)
            binding.composeViewTitle.setContent {
                WebTitle(titleStr, alpha) {
                    if (!agentWeb.back()) {
                        finish()
                    }
                }
            }
            binding.tbTitleBar.visibility = View.GONE
            binding.composeViewTitle.visibility = View.VISIBLE
        } else {
            BarUtils.setStatusBarLightMode(this,true)
            binding.composeViewTitle.visibility = View.GONE
            binding.tbTitleBar.visibility = View.VISIBLE
            binding.tbTitleBar.setContent {
                CommonTitleView(
                    title = titleStr,
                    titleBackgroundColor = Color.Transparent,
                    backClick = {
                        if (!agentWeb.back()) {
                            finish()
                        }
                    })
            }
        }
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.linerView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ))
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


        agentWeb.webCreator.webView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (hideTitle.isNotEmpty()){
                alpha = scrollY.toFloat() / (BarUtils.getStatusBarHeight() + dp2px(50f))
                BarUtils.setStatusBarLightMode(this,if(alpha>=1) true else false)
            }
        }
    }

    override fun initListener() {

    }

}