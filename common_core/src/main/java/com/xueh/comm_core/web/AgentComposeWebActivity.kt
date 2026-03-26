package com.xueh.comm_core.web

import android.content.Intent
import androidx.compose.runtime.Composable
import com.blankj.utilcode.util.ActivityUtils
import com.xueh.comm_core.base.compose.BaseComposeActivity

/**
 * 使用 Compose 承载的 **AgentWeb** 页：通过 [start] 传入 URL 与标题，内部 [AgentWebScaffold] 处理生命周期与返回。
 *
 * **流程**：`ActivityUtils.startActivity` → 读取 [TITLE]/[URL] → 全屏 Scaffold（无默认 [BaseComposeActivity] 标题栏）。
 */
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
