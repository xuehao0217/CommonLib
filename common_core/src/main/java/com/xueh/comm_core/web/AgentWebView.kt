package com.xueh.comm_core.web

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.MiddlewareWebChromeBase

class AgentWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    url: String
) : FrameLayout(context, attrs) {

    private var agentWeb: AgentWeb
    init {
        agentWeb = AgentWeb.with(ActivityUtils.getTopActivity())
            .setAgentWebParent(this, LayoutParams(-1, -1))
            .useDefaultIndicator()
            .interceptUnkownUrl()
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .useMiddlewareWebChrome(object : MiddlewareWebChromeBase() {
                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
//                    if (!TextUtils.isEmpty(title) && titleStr.isEmpty()) {
//                        if (title.length > 10) {
//                            titleStr = title.substring(0, 10) + "..."
//                        }
//                        titleStr = title
//                    }
                }
            })
            .createAgentWeb()
            .ready()
            .go(url)

        LogUtils.e("AgentWebView","url==${url}")
    }

    fun loadUrl(url:String){
        agentWeb.webCreator.webView.loadUrl(url)
    }

}