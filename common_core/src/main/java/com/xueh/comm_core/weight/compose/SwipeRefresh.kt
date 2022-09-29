package com.xueh.comm_core.weight.compose

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.loren.component.view.composesmartrefresh.*

/**
 * 创 建 人: xueh
 * 创建日期: 2022/9/29
 * 备注：
 */
@Composable
fun SwipeRefresh(
    isRefreshing: Boolean,//是否下拉刷新
    scrollState: LazyListState = rememberLazyListState(),//滑动状态
    refreshState: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),//下拉刷新状态
    isNeedLoadMore: Boolean = false,
    isNeedRefresh: Boolean = true,
    headerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState.refreshFlag) },
    footerIndicator: @Composable () -> Unit = { MyRefreshHeader(refreshState.refreshFlag) },
    onRefresh: (suspend () -> Unit)? = null,
    onLoadMore: (suspend () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(refreshState.smartSwipeRefreshAnimateFinishing) {
        if (refreshState.smartSwipeRefreshAnimateFinishing.isFinishing && !refreshState.smartSwipeRefreshAnimateFinishing.isRefresh) {
            scrollState.animateScrollToItem(scrollState.firstVisibleItemIndex + 1)
        }
    }
    if (isRefreshing) {
        refreshState.refreshFlag = SmartSwipeStateFlag.REFRESHING
    } else {
        refreshState.refreshFlag = SmartSwipeStateFlag.SUCCESS
    }
    SmartSwipeRefresh(
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        isNeedLoadMore = isNeedLoadMore,
        state = refreshState,
        isNeedRefresh = isNeedRefresh,
        headerIndicator = headerIndicator,
        footerIndicator = footerIndicator,
        content = content
    )

}