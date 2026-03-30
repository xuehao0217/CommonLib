/**
 * 分页列表与下拉刷新封装：在 [LazyPagingItems] 上组合 [pullToRefresh]、竖直 [VerticalPager] 等，减少页面样板代码。
 *
 * **布局约定**：[PagingLazyColumn]、[PagingVerticalGrid]、[PagingVerticalStaggeredGrid] 在组合树中与 [pagingRefreshStateContent] 为**兄弟**节点（无内置 `Box` 叠放）。
 * 若父级为纵向 `Column`，刷新占位会画在列表**下方**；需要「全屏空/错态盖在列表上」时，请在外层自行使用 `Box(Modifier.fillMaxSize())` 等包裹，或给列表加 `Modifier.weight(1f)` 等约束。
 */
package com.xueh.comm_core.widget

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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import com.xueh.comm_core.helper.compose.onScrollStopVisibleList

@Composable
private fun <T : Any> DefaultPagingAppendStateContent(items: LazyPagingItems<T>) {
    items.PagingStateAppend()
}

@Composable
private fun <T : Any> DefaultPagingRefreshStateContent(items: LazyPagingItems<T>) {
    items.PagingStateRefresh()
}

/**
 * 将 [LazyListState] / Grid 返回的 **整条 Lazy 子项下标** 映射为分页项：仅统计落在
 * `[pagingStartLazyIndex, pagingStartLazyIndex + itemCount)` 内的可见格，与 [CommonLazyColumn] 可选 head、
 * [PagingAppendItem]、foot 之后的真实顺序一致（分页 cell 在 LazyColumn 中为连续一段）。
 */
@Composable
private fun <T : Any> LazyPagingItems<T>.ObserveVisibleItemsOnScrollStop(
    onStop: ((List<T>) -> Unit)?,
    pagingStartLazyIndex: Int = 0,
    observeBlock: @Composable (onIndexes: (List<Int>) -> Unit) -> Unit,
) {
    if (onStop == null) return
    observeBlock { visibleLazyIndexes ->
        val visible = visibleLazyIndexes.toSet()
        val out = buildList {
            for (pagingIdx in 0 until itemCount) {
                val lazyIdx = pagingStartLazyIndex + pagingIdx
                if (lazyIdx !in visible) continue
                this@ObserveVisibleItemsOnScrollStop[pagingIdx]?.let { add(it) }
            }
        }
        onStop(out)
    }
}

/**
 * 纵向分页列表（[CommonLazyColumn]：可选 [headContent]、分页区、[PagingAppendItem]、可选 [footContent]）。
 *
 * [onScrollStopVisibleList]：滚动停止时回调**当前可见**的非空分页项；已按 [headContent] 对 Lazy 下标做偏移，与 append/foot 无冲突。
 *
 * [pagingRefreshStateContent] 与列表为兄弟组合，见文件头「布局约定」。
 */
@Composable
fun <T : Any> LazyPagingItems<T>.PagingLazyColumn(
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: ((index: Int) -> Any)? = null,
    contentType: ((index: Int) -> Any?)? = null,
    headContent: (@Composable () -> Unit)? = null,
    footContent: (@Composable () -> Unit)? = null,
    pagingAppendStateContent: (@Composable () -> Unit)? = null,
    pagingRefreshStateContent: (@Composable () -> Unit)? = null,
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    val pagingStartLazyIndex = if (headContent != null) 1 else 0
    val appendContent = pagingAppendStateContent
    val refreshContent = pagingRefreshStateContent
    ObserveVisibleItemsOnScrollStop(
        onStop = onScrollStopVisibleList,
        pagingStartLazyIndex = pagingStartLazyIndex,
    ) { callback ->
        lazyListState.onScrollStopVisibleList(callback)
    }

    CommonLazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        headContent = headContent,
        footContent = footContent,
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) },
        ) { index ->
            this@PagingLazyColumn[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingLazyColumn) {
            if (appendContent != null) appendContent()
            else DefaultPagingAppendStateContent(this@PagingLazyColumn)
        }
    }
    if (refreshContent != null) refreshContent()
    else DefaultPagingRefreshStateContent(this)
}

/**
 * 分页网格；Lazy 子项顺序为分页 cell 后接 [PagingAppendItem]（无 head），[onScrollStopVisibleList] 使用默认 `pagingStartLazyIndex = 0`。
 *
 * [pagingRefreshStateContent] 与网格为兄弟组合，见文件头「布局约定」。
 */
@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    key: ((index: Int) -> Any)? = null,
    contentType: ((index: Int) -> Any?)? = null,
    pagingAppendStateContent: (@Composable () -> Unit)? = null,
    pagingRefreshStateContent: (@Composable () -> Unit)? = null,
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable LazyGridItemScope.(T) -> Unit,
) {
    val appendContent = pagingAppendStateContent
    val refreshContent = pagingRefreshStateContent
    ObserveVisibleItemsOnScrollStop(onStop = onScrollStopVisibleList) { callback ->
        state.onScrollStopVisibleList(callback)
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columns),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) },
        ) { index ->
            this@PagingVerticalGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalGrid) {
            if (appendContent != null) appendContent()
            else DefaultPagingAppendStateContent(this@PagingVerticalGrid)
        }
    }
    if (refreshContent != null) refreshContent()
    else DefaultPagingRefreshStateContent(this)
}

/**
 * 分页瀑布流网格；子项顺序同 [PagingVerticalGrid]。
 *
 * [pagingRefreshStateContent] 与网格为兄弟组合，见文件头「布局约定」。
 */
@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalStaggeredGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(7.dp),
    verticalItemSpacing: Dp = 0.dp,
    key: ((index: Int) -> Any)? = null,
    contentType: ((index: Int) -> Any?)? = null,
    pagingAppendStateContent: (@Composable () -> Unit)? = null,
    pagingRefreshStateContent: (@Composable () -> Unit)? = null,
    onScrollStopVisibleList: ((List<T>) -> Unit)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(T) -> Unit,
) {
    val appendContent = pagingAppendStateContent
    val refreshContent = pagingRefreshStateContent
    ObserveVisibleItemsOnScrollStop(onStop = onScrollStopVisibleList) { callback ->
        state.onScrollStopVisibleList(callback)
    }

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(columns),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalItemSpacing = verticalItemSpacing,
    ) {
        items(
            count = itemCount,
            key = key,
            contentType = { contentType?.invoke(it) },
        ) { index ->
            this@PagingVerticalStaggeredGrid[index]?.let { itemContent(it) }
        }
        PagingAppendItem(this@PagingVerticalStaggeredGrid) {
            if (appendContent != null) appendContent()
            else DefaultPagingAppendStateContent(this@PagingVerticalStaggeredGrid)
        }
    }
    if (refreshContent != null) refreshContent()
    else DefaultPagingRefreshStateContent(this)
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingVerticalPager(
    state: PagerState = rememberPagerState { itemCount },
    modifier: Modifier = Modifier,
    key: ((index: Int) -> Any)? = null,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pagingRefreshStateContent: (@Composable () -> Unit)? = null,
    pageContent: @Composable PagerScope.(T) -> Unit,
) {
    val refreshContent = pagingRefreshStateContent
    LaunchedEffect(itemCount) {
        if (itemCount > 0 && state.currentPage >= itemCount) {
            state.animateScrollToPage(itemCount - 1)
        }
    }
    VerticalPager(
        modifier = modifier,
        state = state,
        key = key,
        beyondViewportPageCount = beyondViewportPageCount,
    ) { index ->
        this@PagingVerticalPager[index]?.let { pageContent(it) }
    }
    if (refreshContent != null) refreshContent()
    else DefaultPagingRefreshStateContent(this)
}

@Composable
fun <T : Any> LazyPagingItems<T>.PagingRefresh(
    modifier: Modifier = Modifier,
    refreshHeader: @Composable ((isRefreshing: Boolean) -> Unit)? = null,
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
        loadMoreEnabled = false,
        onRefresh = { refresh() },
        onLoadMore = {},
        modifier = modifier,
        headerScrollMode = NestedScrollMode.Translate,
        footerScrollMode = NestedScrollMode.Translate,
        headerIndicator = {
            refreshHeader?.invoke(isRefreshing) ?: ClassicRefreshHeader(it)
        },
        footerIndicator = {},
    ) {
        currentItems.value(this)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T : Any> LazyPagingItems<T>.PagingRefreshColumn(
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    refreshHeader: @Composable AnimatedVisibilityScope.() -> Unit = {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    },
    onContent: @Composable BoxScope.(LazyPagingItems<T>) -> Unit,
) {
    val isRefreshing = isRefreshing()
    Column(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
            .pullToRefresh(state = state, isRefreshing = isRefreshing) { refresh() },
    ) {
        AnimatedVisibility(
            visible = isRefreshing,
            enter = fadeIn() + scaleIn() + expandVertically(),
            exit = fadeOut() + scaleOut() + shrinkVertically(),
        ) {
            refreshHeader()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            onContent(this@PagingRefreshColumn)
        }
    }
}
