package com.xueh.comm_core.web

import android.content.Intent
import androidx.compose.runtime.Composable
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils
import com.xueh.comm_core.base.compose.BaseComposeActivity
import java.util.BitSet

class WebViewComposeActivity : BaseComposeActivity() {

    companion object {
        const val URL = "url"
        fun start(url: String) {
            ActivityUtils.startActivity(
                Intent(
                    ActivityUtils.getTopActivity(),
                    WebViewComposeActivity::class.java
                ).apply {
                    putExtra(URL, url)
                })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    @Composable
    override fun setComposeContent() {
        WebViewPage("${intent?.getStringExtra(URL)}") {
            this.finish()
        }
    }
}