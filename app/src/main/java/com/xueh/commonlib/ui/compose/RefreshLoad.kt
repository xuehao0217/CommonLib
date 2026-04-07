package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
import com.xueh.comm_core.widget.PagingLazyColumn
import com.xueh.comm_core.widget.PagingRefresh
import com.xueh.comm_core.widget.PagingStateAppend
import com.xueh.comm_core.widget.PagingStateRefresh
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel

/**
 * 下拉刷新 + 分页列表示例（Material 3 子页面样式）。
 */
@Composable
fun RefreshLoadUse() {
    BaseComposeViewModel<ComposeViewModel> { viewModel ->
        val homeDatas = viewModel.getListDatas().collectAsLazyPagingItems()
        val lazyListState = rememberLazyListState()
        val scheme = MaterialTheme.colorScheme

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(scheme.surface),
        ) {
            DemoScreenIntro(
                text = "在列表顶部下拉触发刷新；滑到底部加载更多。数据来自演示接口分页。",
            )

            homeDatas.PagingRefresh(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                homeDatas.PagingLazyColumn(
                    lazyListState = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    key = homeDatas.itemKey { it.id },
                    pagingRefreshStateContent = {
                        homeDatas.PagingStateRefresh(
                            stateEmpty = {
                                RefreshLoadEmptyPanel()
                            },
                            stateError = { err ->
                                RefreshLoadRefreshErrorPanel(
                                    message = err.localizedMessage,
                                    onRetry = { homeDatas.retry() },
                                )
                            },
                        )
                    },
                    pagingAppendStateContent = {
                        homeDatas.PagingStateAppend(
                            stateError = { err ->
                                RefreshLoadAppendErrorBar(
                                    message = err.localizedMessage,
                                    onRetry = { homeDatas.retry() },
                                )
                            },
                            stateNoMore = {
                                RefreshLoadNoMoreFooter()
                            },
                        )
                    },
                ) { item ->
                    DemoArticleListCard(
                        idText = "${item.id}",
                        title = item.title,
                    )
                }
            }
        }
    }
}

@Composable
private fun RefreshLoadEmptyPanel() {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Inbox,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = scheme.onSurfaceVariant,
            )
            Text(
                text = "暂无数据",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = "下拉页面顶部可尝试刷新",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun RefreshLoadRefreshErrorPanel(
    message: String?,
    onRetry: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.errorContainer),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = scheme.error,
            )
            Text(
                text = "刷新失败",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onErrorContainer,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = message?.takeIf { it.isNotBlank() } ?: "请检查网络后重试",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            FilledTonalButton(
                onClick = onRetry,
                modifier = Modifier.padding(top = 20.dp),
            ) {
                Text(text = "重试")
            }
        }
    }
}

@Composable
private fun RefreshLoadAppendErrorBar(
    message: String?,
    onRetry: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onRetry),
        shape = RoundedCornerShape(12.dp),
        color = scheme.errorContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = scheme.error,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "加载更多失败",
                    style = MaterialTheme.typography.titleSmall,
                    color = scheme.onErrorContainer,
                )
                Text(
                    text = message?.takeIf { it.isNotBlank() } ?: "点击此处重试",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onErrorContainer,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun RefreshLoadNoMoreFooter() {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 48.dp),
            color = scheme.outlineVariant,
        )
        Text(
            text = "没有更多了",
            style = MaterialTheme.typography.labelMedium,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
