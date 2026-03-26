package com.xueh.comm_core.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.xueh.comm_core.R

// -------------------- Refresh --------------------

fun LazyPagingItems<*>.isRefreshing() = loadState.refresh is LoadState.Loading

fun LazyPagingItems<*>.isRefreshError() = loadState.refresh is LoadState.Error

/**
 * 首屏/刷新区域状态：无数据时根据 [loadState.refresh] 展示。
 * [initialEmptyContent]：刷新已完成、列表仍为空且非错误时的展示（例如占位文案）。
 */
@Composable
fun LazyPagingItems<*>.PagingStateRefresh(
    stateLoading: @Composable () -> Unit = { PagingRefreshLoading() },
    stateError: @Composable (Throwable) -> Unit = { PagingRefreshError(this) },
    stateEmpty: @Composable () -> Unit = { PagingRefreshEmpty() },
    initialEmptyContent: @Composable () -> Unit = stateEmpty,
) {
    if (itemCount != 0) return
    when (val refreshState = loadState.refresh) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(refreshState.error)
        is LoadState.NotLoading -> {
            if (refreshState.endOfPaginationReached) {
                stateEmpty()
            } else {
                initialEmptyContent()
            }
        }
    }
}

// -------------------- Append --------------------

fun LazyPagingItems<*>.isStateAppend() = itemCount > 0

@Composable
fun LazyPagingItems<*>.PagingStateAppend(
    stateLoading: @Composable () -> Unit = { PagingAppendLoading() },
    stateError: @Composable (Throwable) -> Unit = { PagingAppendError(this) },
    stateNoMore: @Composable () -> Unit = { PagingAppendNoMore() },
) {
    if (!isStateAppend()) return
    when (val appendState = loadState.append) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(appendState.error)
        is LoadState.NotLoading -> if (appendState.endOfPaginationReached && itemCount > 0) stateNoMore()
    }
}

// -------------------- Prepend --------------------

fun LazyPagingItems<*>.isStatePrepend(): Boolean =
    itemCount > 0 && !isRefreshing() && !loadState.prepend.endOfPaginationReached

@Composable
fun LazyPagingItems<*>.PagingStatePrepend(
    stateLoading: @Composable () -> Unit = { PagingAppendLoading() },
    stateError: @Composable (Throwable) -> Unit = { PagingAppendError(this) },
) {
    when (val prependState = loadState.prepend) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(prependState.error)
        else -> {}
    }
}

// -------------------- Lazy scopes Append Item --------------------

fun LazyListScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit = { items.PagingStateAppend() },
) {
    if (items.isStateAppend()) item(key = key ?: "append_item", contentType = contentType, content = content)
}

fun LazyGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    span: (LazyGridItemSpanScope.() -> GridItemSpan) = { GridItemSpan(maxLineSpan) },
    content: @Composable LazyGridItemScope.() -> Unit = { items.PagingStateAppend() },
) {
    if (items.isStateAppend()) item(key = key ?: "append_item", contentType = contentType, span = span, content = content)
}

fun LazyStaggeredGridScope.PagingAppendItem(
    items: LazyPagingItems<*>,
    key: Any? = null,
    contentType: Any? = null,
    span: StaggeredGridItemSpan = StaggeredGridItemSpan.FullLine,
    content: @Composable LazyStaggeredGridItemScope.() -> Unit = { items.PagingStateAppend() },
) {
    if (items.isStateAppend()) item(key = key ?: "append_item", contentType = contentType, span = span, content = content)
}

// -------------------- Default widgets --------------------

@Composable
fun PagingRefreshError(item: LazyPagingItems<*>) {
    val errTitle = stringResource(R.string.comm_core_paging_refresh_error)
    val retry = stringResource(R.string.comm_core_paging_retry)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = errTitle,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error,
            )
            Text(text = errTitle, modifier = Modifier.padding(top = 10.dp))
            Button(onClick = { item.retry() }, modifier = Modifier.padding(top = 10.dp)) {
                Text(text = retry)
            }
        }
    }
}

@Composable
fun PagingRefreshLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}

@Composable
fun PagingRefreshEmpty() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.comm_core_paging_empty))
    }
}

@Composable
fun PagingAppendError(item: LazyPagingItems<*>) {
    Text(
        text = stringResource(R.string.comm_core_paging_append_error),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { item.retry() },
        textAlign = TextAlign.Center,
    )
}

@Composable
fun PagingAppendNoMore() {
    Text(
        text = stringResource(R.string.comm_core_paging_no_more),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun PagingAppendLoading() {
    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}
