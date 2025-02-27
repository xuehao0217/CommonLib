package com.xueh.commonlib.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.weight.compose.click

class TestComposeActivity:BaseComposeActivity() {
    @Composable
    override fun setComposeContent() {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue).click {
                isSystemBarLight=!isSystemBarLight
            })

    }

    override fun setTitle()="TestComposeActivity"
}
