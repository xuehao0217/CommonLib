package com.xueh.comm_core.web

import android.content.Intent
import androidx.compose.runtime.Composable
import com.blankj.utilcode.util.ActivityUtils
import com.xueh.comm_core.base.compose.BaseComposeActivity

class AgentComposeWebActivity : BaseComposeActivity() {
    companion object {
        const val TITLE = "title"
        const val URL = "url"

        fun start(url: String, title: String = "") {
            ActivityUtils.startActivity(
                Intent(
                    ActivityUtils.getTopActivity(),
                    AgentComposeWebActivity::class.java,
                ).apply {
                    putExtra(TITLE, title)
                    putExtra(URL, url)
                },
            )
        }
    }

    override fun showTitleView() = false

    @Composable
    override fun setComposeContent() {
        val url = intent.getStringExtra(URL) ?: ""
        val title = intent.getStringExtra(TITLE) ?: ""
        AgentWebScaffold(
            activity = this,
            url = url,
            initialTitle = title,
            onClose = { finish() },
        )
    }
}
