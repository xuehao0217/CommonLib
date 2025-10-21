package com.xueh.comm_core.weight.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshFooter
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import com.xueh.comm_core.helper.compose.onScrollStopVisibleList
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberUpdatedState

// ==========================================
// LazyColumn
// ==========================================
@Composable
fun <T : Any> LazyPagingItems<T>.PagingLazyColumn(
    lazyListState: LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: ((index: Int) -> Any)? = null,
    headContent: @Composable () -> Unit = {},
    footContent: @Composable () -> Unit = {},
    pagingAppendStateContent: @Composable () -> Unit = {},
    pagingRefreshStateContent: @Composable () -> Unit = {},
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    if (onScrollStopVisibleList != null) {
        lazyListState.onScrollStopVisibleList { visibleIndexes ->
            itemSnapshotList.items.filterIndexed { index, _ -> index in visibleIndexes }
                .let { onScrollStopVisibleList(it) }
        }
    }

    CommonLazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        headContent = headContent,
        footContent = footContent,
    ) {
        items(count = itemCount, key = key) { index ->
            this@PagingLazyColumn[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingLazyColumn) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

// ==========================================
// LazyVerticalGrid
// ==========================================
@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyGridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    key: ((index: Int) -> Any)? = null,
    pagingAppendStateContent: @Composable () -> Unit = {},
    pagingRefreshStateContent: @Composable () -> Unit = {},
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable androidx.compose.foundation.lazy.grid.LazyGridItemScope.(T) -> Unit,
) {
    if (onScrollStopVisibleList != null) {
        state.onScrollStopVisibleList { visibleIndexes ->
            itemSnapshotList.items.filterIndexed { index, _ -> index in visibleIndexes }
                .let { onScrollStopVisibleList(it) }
        }
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement
    ) {
        items(count = itemCount, key = key) { index ->
            this@PagingVerticalGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalGrid) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

// ==========================================
// LazyVerticalStaggeredGrid
// ==========================================
@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalStaggeredGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyStaggeredGridState = androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    verticalItemSpacing: Dp = 0.dp,
    key: ((index: Int) -> Any)? = null,
    pagingAppendStateContent: @Composable () -> Unit = {},
    pagingRefreshStateContent: @Composable () -> Unit = {},
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope.(T) -> Unit,
) {
    if (onScrollStopVisibleList != null) {
        state.onScrollStopVisibleList { visibleIndexes ->
            itemSnapshotList.items.filterIndexed { index, _ -> index in visibleIndexes }
                .let { onScrollStopVisibleList(it) }
        }
    }

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells.Fixed(columns),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalItemSpacing = verticalItemSpacing
    ) {
        items(count = itemCount, key = key) { index ->
            this@PagingVerticalStaggeredGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalStaggeredGrid) { pagingAppendStateContent() }
    }
    pagingRefreshStateContent()
}

// ==========================================
// VerticalPager
// ==========================================
@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalPager(
    state: PagerState = rememberPagerState { itemCount },
    modifier: Modifier = Modifier,
    key: ((index: Int) -> Any)? = null,
    beyondViewportPageCount: Int = androidx.compose.foundation.pager.PagerDefaults.BeyondViewportPageCount,
    pagingRefreshStateContent: @Composable () -> Unit = {},
    pageContent: @Composable PagerScope.(T) -> Unit,
) {
    VerticalPager(
        modifier = modifier,
        state = state,
        key = key,
        beyondViewportPageCount = beyondViewportPageCount
    ) { index ->
        this@PagingVerticalPager[index]?.let { pageContent(it) }
    }
    pagingRefreshStateContent()
}

// ==========================================
// Refresh
// ==========================================
@Composable
fun <T : Any> LazyPagingItems<T>.PagingRefresh(
    modifier: Modifier = Modifier,
    refreshHeader: @Composable ((isRefreshing: Boolean) -> Unit)? = null,
    refreshFooter: @Composable (() -> Unit)? = null,
    onContent: @Composable (LazyPagingItems<T>) -> Unit,
) {
    val state = rememberUltraSwipeRefreshState()
    val isRefreshing = isRefreshing()
    val currentItems = rememberUpdatedState(onContent)

    LaunchedEffect(isRefreshing) {
        state.isRefreshing = isRefreshing
    }

    UltraSwipeRefresh(
        state = state,
        loadMoreEnabled = refreshFooter != null,
        onRefresh = { refresh() },
        onLoadMore = {},
        modifier = modifier,
        headerScrollMode = NestedScrollMode.Translate,
        footerScrollMode = NestedScrollMode.Translate,
        headerIndicator = {
            refreshHeader?.invoke(isRefreshing) ?: ClassicRefreshHeader(it)
        },
        footerIndicator = {
            refreshFooter?.invoke() ?: ClassicRefreshFooter(it)
        }
    ) {
        currentItems.value(this)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T : Any> LazyPagingItems<T>.PagingRefreshColumn(
    state: PullToRefreshState = rememberPullToRefreshState(),
    refreshHeader: @Composable AnimatedVisibilityScope.() -> Unit,
    onContent: @Composable BoxScope.(LazyPagingItems<T>) -> Unit,
) {
    val isRefreshing = isRefreshing()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
            .pullToRefresh(state = state, isRefreshing = isRefreshing) { refresh() }
    ) {
        AnimatedVisibility(
            visible = isRefreshing,
            enter = fadeIn() + scaleIn() + expandVertically(),
            exit = fadeOut() + scaleOut() + shrinkVertically()
        ) {
            refreshHeader()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            onContent(this@PagingRefreshColumn)
        }
    }
}




