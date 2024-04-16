package com.xueh.commonlib.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.weight.compose.CommonTitlePage

/**
 * 创 建 人: xueh
 * 创建日期: 2023/8/30
 * 备注： https://github.com/KatieBarnett/Experiments/tree/main/jc-edge-to-edge?source=post_page-----bea553dd97ff--------------------------------
 */
class ComposeActivity:BaseComposeActivity() {
    @Composable
    override fun setComposeContent() {
        CommonTitlePage(title = "Compose",showBackIcon = false) {
           Box(modifier = Modifier.fillMaxSize().background(Color.Blue))
        }
    }
}