/**
 * 列表封装：[CommonLazyColumn]、[CommonLazyColumnData] 等，统一间距、内边距与可选头尾插槽。
 */
package com.xueh.comm_core.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonLazyColumn(
    modifier: Modifier = Modifier.fillMaxSize(),
    state: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    headContent: (@Composable () -> Unit)? = null,
    footContent: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
    ) {
        headContent?.let { item { it() } }
        content()
        footContent?.let { item { it() } }
    }
}

/**
 * @param itemContent 若行内需调用随重组频繁变化的回调，调用方应在 Composable 内用 [androidx.compose.runtime.rememberUpdatedState] 包一层再读。
 */
@Composable
fun <T> CommonLazyColumnData(
    data: List<T>,
    modifier: Modifier = Modifier.fillMaxSize(),
    state: LazyListState = rememberLazyListState(),
    key: ((item: T) -> Any)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headContent: (@Composable () -> Unit)? = null,
    footContent: (@Composable () -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
    CommonLazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
        headContent = headContent,
        footContent = footContent,
    ) {
        items(data, key = key) { item ->
            itemContent(item)
        }
    }
}
