package com.xueh.comm_core.weight.compose

import android.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.loren.component.view.composesmartrefresh.SmartSwipeRefreshState
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.xueh.comm_core.helper.compose.onScrollStopVisibleList
import com.xueh.comm_core.helper.isEmpty
import com.xueh.comm_core.weight.compose.refreshheader.MyRefreshHeader

@Composable
fun <T : Any> PagingRefreshList(
    lazyPagingItems: LazyPagingItems<T>,
    isFirstRefresh: Boolean = true,
    key: ((index: Int) -> Any)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    pagingEmptyContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingErrorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {
    //是不是在loading
    val isRefreshing = lazyPagingItems.isRefreshing()
    SmartRefresh(
        isFirstRefresh = isFirstRefresh,
        isRefreshing = isRefreshing,
        scrollState = lazyListState,
        refreshState = refreshState,
        headerIndicator = headerIndicator,
        onRefresh = {
            lazyPagingItems.refresh()
        }) {
        PagingLazyColumn(
            lazyPagingItems = lazyPagingItems,
            lazyListState = lazyListState,
            key = key,
            verticalArrangement = verticalArrangement,
            contentPadding = contentPadding,
            pagingEmptyContent = pagingEmptyContent,
            pagingLoadingContent = pagingLoadingContent,
            pagingErrorContent = pagingErrorContent,
            headContent = headContent,
            foodContent = foodContent,
            itemContent = itemContent
        )
    }
}

@Composable
fun <T : Any> PagingLazyColumn(
    lazyPagingItems: LazyPagingItems<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(15.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    key: ((index: Int) -> Any)? = null,
    headContent: @Composable () -> Unit? = {},
    foodContent: @Composable () -> Unit? = {},
    onScrollStopVisibleList: ((list: List<T>) -> Unit)? = null,
    pagingEmptyContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingErrorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    pagingAppendStateContent: @Composable (LazyItemScope.() -> Unit) = {
        lazyPagingItems.PagingStateAppend()
    },
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    if (onScrollStopVisibleList != null) {
        lazyListState.onScrollStopVisibleList {
            if (lazyPagingItems.itemSnapshotList.items.isNotEmpty()) {
                lazyPagingItems.itemSnapshotList.items.filterIndexed { index, _ -> index in it }
                    .let {
                        onScrollStopVisibleList.invoke(it)
                    }
            }
        }
    }
    lazyPagingItems.PagingBaseBox(
        pagingEmptyContent = pagingEmptyContent,
        pagingLoadingContent = pagingLoadingContent,
        pagingErrorContent = pagingErrorContent,
    ) {
        CommonLazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            headContent = headContent,
            foodContent = foodContent,
        ) {
            // 如果是老版本的Paging3这里的实现方式不同，自己根据版本来实现。
            items(lazyPagingItems.itemCount, key = key) { index ->
                lazyPagingItems[index]?.let {
                    itemContent(it)
                }
            }
            PagingAppendItem(lazyPagingItems) {
                pagingAppendStateContent()
            }
        }
    }
}

/*
*  key = lazyPagingItems.itemKey { it.id }
* */
@Composable
fun <T : Any> PagingVerticalGrid(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyGridState = rememberLazyGridState(),
    pagingEmptyContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingErrorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    pagingAppendStateContent: @Composable (LazyGridItemScope.() -> Unit) = { lazyPagingItems.PagingStateAppend() },
    onScrollStopVisibleList: ((list: List<T>) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(T) -> Unit
) {
    if (onScrollStopVisibleList != null) {
        state.onScrollStopVisibleList {
            if (lazyPagingItems.itemSnapshotList.items.isNotEmpty()) {
                lazyPagingItems.itemSnapshotList.items.filterIndexed { index, _ -> index in it }
                    .let {
                        onScrollStopVisibleList.invoke(it)
                    }
            }
        }
    }
    lazyPagingItems.PagingBaseBox(
        pagingEmptyContent = pagingEmptyContent,
        pagingLoadingContent = pagingLoadingContent,
        pagingErrorContent = pagingErrorContent,
    ) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(columns), state = state, contentPadding = contentPadding,
            horizontalArrangement = horizontalArrangement
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = key,
            ) {
                lazyPagingItems[it]?.let {
                    itemContent(it)
                }
            }
            PagingAppendItem(lazyPagingItems) {
                pagingAppendStateContent()
            }
        }
    }
}


/*
*  key = lazyPagingItems.itemKey { it.id }
* */
@Composable
fun <T : Any> PagingVerticalStaggeredGrid(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    pagingEmptyContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingErrorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    columns: Int = 2,
    pagingAppendStateContent: @Composable (LazyStaggeredGridItemScope.() -> Unit) = {
        lazyPagingItems.PagingStateAppend()
    },
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onScrollStopVisibleList: ((list: List<T>) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    verticalItemSpacing: Dp = 0.dp,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(T) -> Unit
) {
    if (onScrollStopVisibleList != null) {
        state.onScrollStopVisibleList {
            if (lazyPagingItems.itemSnapshotList.items.isNotEmpty()) {
                lazyPagingItems.itemSnapshotList.items.filterIndexed { index, _ -> index in it }
                    .let {
                        onScrollStopVisibleList.invoke(it)
                    }
            }
        }
    }
    lazyPagingItems.PagingBaseBox(
        pagingEmptyContent = pagingEmptyContent,
        pagingLoadingContent = pagingLoadingContent,
        pagingErrorContent = pagingErrorContent,
    ) {
        LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = StaggeredGridCells.Fixed(columns),
            state = state,
            contentPadding = contentPadding,
            verticalItemSpacing = verticalItemSpacing,
            horizontalArrangement = horizontalArrangement
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = key,
            ) {
                lazyPagingItems[it]?.let {
                    itemContent(it)
                }
            }
            PagingAppendItem(lazyPagingItems) {
                pagingAppendStateContent()
            }
        }
    }
}


/*
*  key = lazyPagingItems.itemKey { it.id }
*  val pagerState = rememberPagerState { lazyPagingItems.itemCount }
* */
@Composable
fun <T : Any> PagingVerticalPager(
    lazyPagingItems: LazyPagingItems<T>,
    state: PagerState = rememberPagerState { lazyPagingItems.itemCount },
    modifier: Modifier = Modifier,
    pagingEmptyContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    pagingErrorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    key: ((index: Int) -> Any)? = null,
    pageContent: @Composable PagerScope.(T) -> Unit
) {
    lazyPagingItems.PagingBaseBox(
        pagingEmptyContent = pagingEmptyContent,
        pagingLoadingContent = pagingLoadingContent,
        pagingErrorContent = pagingErrorContent,
    ) {

        VerticalPager(
            modifier = modifier,
            state = state,
            key = key
        ) { index ->
            lazyPagingItems[index]?.let {
                pageContent(it)
            }
        }
    }
}


@Composable
fun <T : Any> SmartRefreshPaging(
    lazyPagingItems: LazyPagingItems<T>,
    isFirstRefresh: Boolean = true,
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState) },
    content: @Composable () -> Unit,
) {
    //是不是在loading
    val isRefreshing = lazyPagingItems.isRefreshing()
    SmartRefresh(
        isFirstRefresh = isFirstRefresh,
        isRefreshing = isRefreshing,
        refreshState = refreshState,
        headerIndicator = headerIndicator,
        onRefresh = {
            lazyPagingItems.refresh()
        }) {
        content()
    }
}


