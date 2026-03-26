package com.xueh.commonlib.ui.compose.google

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.xueh.commonlib.navigation.DemoCustomPullRefreshSample
import com.xueh.commonlib.navigation.DemoPullRefreshIndicatorTransform
import com.xueh.commonlib.navigation.DemoPullRefreshSample
import com.xueh.commonlib.ui.compose.DemoListRow

@Composable
fun GoogleSamplePage(onNavigate: (NavKey) -> Unit) {
    Column(Modifier.padding(top = 8.dp)) {
        Text(
            text = "Google 官方示例",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 4.dp),
        )
        DemoListRow(title = "PullRefreshSample") {
            onNavigate(DemoPullRefreshSample)
        }
        DemoListRow(title = "CustomPullRefreshSample") {
            onNavigate(DemoCustomPullRefreshSample)
        }
        DemoListRow(title = "PullRefreshIndicatorTransformSample") {
            onNavigate(DemoPullRefreshIndicatorTransform)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GoogleSamplePagePreview() {
    GoogleSamplePage(onNavigate = {})
}
