package com.xueh.comm_core.web

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.MiddlewareWebChromeBase
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.databinding.ActivityWebViewBinding
import com.xueh.comm_core.weight.compose.CommonTitleView

/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/11 11:16
 * 备注：一个只用于显示的web页面
 */
open class WebViewActivity : DActivity<ActivityWebViewBinding>() {

    companion object {
        const val TITLE = "title"
        const val URL = "url"

        fun start(url: String,title:String="") {
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
    override fun initView(savedInstanceState: Bundle?) {
        BarUtils.addMarginTopEqualStatusBarHeight(binding.tbTitleBar)

        titleStr = "${intent?.getStringExtra(TITLE)}"
        binding.tbTitleBar.setContent {
            CommonTitleView(name = titleStr, backClick = {
                finish()
            })
        }
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.rvWebContent, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .useMiddlewareWebChrome(object : MiddlewareWebChromeBase() {
                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    if (!TextUtils.isEmpty(title)) {
                        if (title.length > 10) {
                            titleStr = title.substring(0, 10) + "..."
                        }
                        titleStr=title
                    }
                }
            })
            .createAgentWeb()
            .ready()
            .go(intent.getStringExtra(URL))
    }

    override fun initListener() {

    }
}