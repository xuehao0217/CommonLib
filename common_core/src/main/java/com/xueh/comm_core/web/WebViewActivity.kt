package com.xueh.comm_core.web

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.xueh.comm_core.R
import com.xueh.comm_core.base.DActivity
import com.xueh.comm_core.databinding.ActivityWebViewBinding
import com.xueh.comm_core.weight.OnTitleLeftListener

/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/11 11:16
 * 备注：一个只用于显示的web页面
 */
open class WebViewActivity : DActivity<ActivityWebViewBinding>() {

    companion object {
        const val TITLE = "title"
        const val URL = "url"
    }


    lateinit var agentWeb: AgentWeb

    public override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
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
        return if (agentWeb!!.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun initDataAfterView() {
        binding.tbTitleBar.title = intent?.getStringExtra(TITLE)
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.rvWebContent, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
//          .setWebChromeClient(mWebChromeClient)
//          .setWebViewClient(mWebViewClient)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .createAgentWeb()
            .ready()
            .go(intent.getStringExtra(URL))
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        binding.tbTitleBar.setOnTitleBarListener(object : OnTitleLeftListener() {
            override fun onLeftClick(v: View) {
                finish()
            }
        })
    }


    private val mWebChromeClient: WebChromeClient =
        object : WebChromeClient() {
            override fun onReceivedTitle(
                view: WebView,
                title: String
            ) {
                var title = title
                super.onReceivedTitle(view, title)
                if ( binding.tbTitleBar != null && !TextUtils.isEmpty(title)) {
                    if (title.length > 10) {
                        title = title.substring(0, 10) + "..."
                    }
                }
                binding.tbTitleBar.title = title
            }
        }
}