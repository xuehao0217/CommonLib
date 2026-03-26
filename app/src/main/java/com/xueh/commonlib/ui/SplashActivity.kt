package com.xueh.commonlib.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ActivityUtils
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.commonlib.BuildConfig
import com.xueh.commonlib.R
import kotlinx.coroutines.delay

/**
 * 启动页：Compose 全屏展示应用名与版本，短暂停留后进入 [MainComposeActivity]。
 */
class SplashActivity : BaseComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppStyle)
        super.onCreate(savedInstanceState)
    }

    override fun showTitleView() = false

    @Composable
    override fun setComposeContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "CommonLib",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "演示工程 · Compose",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "版本 ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                CircularProgressIndicator()
            }
        }
        LaunchedEffect(Unit) {
            delay(900)
            ActivityUtils.startActivity(MainComposeActivity::class.java)
            finish()
        }
    }
}
