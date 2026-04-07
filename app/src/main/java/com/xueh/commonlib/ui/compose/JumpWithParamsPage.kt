package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Navigation 3 下通过上层共享 [resultText] / 回调实现返回传参。
 */
@Composable
fun NavigateParams1View(
    resultText: String,
    onOpenSecond: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        DemoScreenIntro(
            text = "共享 MutableState 模拟上一级收子页回传；底色区为演示态强调。",
        )
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
            text = "上一级：展示子页回传结果",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = TextAlign.Center,
            )
            Box(
                Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .size(width = 280.dp, height = 100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Button(
                onClick = onOpenSecond,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(text = "进入下一页")
            }
        }
    }
}

@Composable
fun NavigateParams2View(
    onDeliverResult: () -> Unit,
    onPop: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        DemoScreenIntro(
            text = "子页按下按钮后先写入共享结果再 pop，上一级可读到最新文案。",
        )
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "子页：回传并关闭",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = {
                    onDeliverResult()
                    onPop()
                },
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text(text = "回传「Hello world」并返回")
            }
            OutlinedButton(
                onClick = {
                    onDeliverResult()
                    onPop()
                },
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text(text = "同上（Outlined）")
            }
        }
    }
}
