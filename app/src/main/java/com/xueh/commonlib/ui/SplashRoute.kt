package com.xueh.commonlib.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xueh.commonlib.BuildConfig
import kotlinx.coroutines.delay

/**
 * 单 Activity 内启动闪屏，结束后通过 [onFinished] 进入主界面。
 */
@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = scheme.surfaceContainerLow,
            tonalElevation = 2.dp,
            shadowElevation = 0.dp,
            modifier = Modifier.padding(horizontal = 36.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 36.dp, vertical = 40.dp),
            ) {
                Text(
                    text = "CommonLib",
                    style = MaterialTheme.typography.headlineMedium,
                    color = scheme.primary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "演示工程 · Compose · 单 Activity",
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "版本 ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.outline,
                    textAlign = TextAlign.Center,
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(40.dp),
                    color = scheme.primary,
                    trackColor = scheme.surfaceContainerHighest,
                    strokeWidth = 3.dp,
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        delay(900)
        onFinished()
    }
}
