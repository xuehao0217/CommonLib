package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.xueh.comm_core.weight.compose.refreshheader.MyRefreshHeader
import com.xueh.comm_core.weight.compose.refreshheader.RefreshHeader

//公用列表
@Composable
inline fun CommonLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    crossinline headContent: @Composable () -> Unit = {},
    crossinline foodContent: @Composable () -> Unit = {},
    crossinline content: LazyListScope.() -> Unit,
) {
    ConstraintLayout(modifier = modifier) {
        val (column, bottom_v) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(column) {
                    top.linkTo(parent.top)
                },
            state = state,
            verticalArrangement = verticalArrangement,
            contentPadding = contentPadding,
        ) {
            item {
                headContent.invoke()
            }
            content(this)
            item {
                foodContent.invoke()
            }
        }
        ShadowVerticalView(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(bottom_v) {
                bottom.linkTo(parent.bottom)
            })
    }
}

//公用数据列表
@Composable
inline fun <T> CommonLazyColumnData(
    data: List<T>,
    modifier: Modifier = Modifier.fillMaxSize(),
    state: LazyListState = rememberLazyListState(),
    noinline key: ((item: T) -> Any)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    crossinline headContent: @Composable () -> Unit = {},
    crossinline foodContent: @Composable () -> Unit = {},
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {

    CommonLazyColumn(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        state = state,
        contentPadding = contentPadding,
        headContent = headContent,
        foodContent = foodContent,
    ) {
        items(data, key = key) {
            itemContent(it)
        }
    }
}


//公用下拉刷新页面
@Composable
fun CommonRefreshPage(
    isRefreshing: Boolean,
    onRefresh: (suspend () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
//    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSmartSwipeRefreshState()
    val listState = rememberLazyListState()
    SmartRefresh(
        isRefreshing = isRefreshing,
        scrollState = listState,
        refreshState = refreshState,
        headerIndicator = { RefreshHeader(refreshState) },
        onRefresh = onRefresh,
        content = content
    )
}


//公用下拉刷新列表数据页面
@Composable
fun <T> CommonRefreshColumnDataPage(
    data: List<T>,
    isRefreshing: Boolean,
    onRefresh: (suspend () -> Unit)? = null,
    key: ((item: T) -> Any)? = null,
    emptContent: @Composable () -> Unit = {},
    headContent: @Composable () -> Unit = {},
    foodContent: @Composable () -> Unit = {},
    itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
//    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSmartSwipeRefreshState()
    val listState = rememberLazyListState()
    SmartRefresh(
        isRefreshing = isRefreshing,
        scrollState = listState,
        refreshState = refreshState,
        headerIndicator = { MyRefreshHeader(refreshState) },
        onRefresh = onRefresh
    ) {
        if (data.isEmpty()) {
            emptContent.invoke()
        } else {
            CommonLazyColumnData(
                data = data,
                headContent = headContent,
                foodContent = foodContent,
                itemContent = itemContent,
                key = key
            )
        }
    }
}