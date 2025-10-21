package com.xueh.commonlib.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.lt.compose_views.util.rememberMutableStateOf
import com.xueh.comm_core.helper.compose.orderedStateMapOf
import com.xueh.comm_core.weight.compose.BoxWrapper
import com.xueh.comm_core.weight.compose.MyScrollableTabRow
import com.xueh.comm_core.weight.compose.PagerTab
import com.xueh.comm_core.weight.compose.PagerTabIndicator
import com.xueh.comm_core.weight.compose.PagingLazyColumn
import com.xueh.comm_core.weight.compose.PagingRefresh
import com.xueh.comm_core.weight.compose.SpacerW
import com.xueh.comm_core.weight.compose.click
import com.xueh.commonlib.entity.HomeEntity
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun TabPage() {
    val viewModel: ComposeViewModel = viewModel()
    var onSelect: (String) -> Unit = {

    }
    var ids = listOf("AAA", "BBB", "CCCCCCCC", "DDDD", "EEEEEEEEEEEEEE", "FFFF", "GGGGGGG")

    var pagingStateList = remember {
        mutableStateListOf<Flow<PagingData<HomeEntity.Data>>>()
    }

    LaunchedEffect(Unit) {
        ids.forEach {
            pagingStateList.add(viewModel.getListDatas())
        }
    }

    TabPageList(ids = ids, pagingStateList)
}

@Composable
fun TabPageList(ids: List<String>, pagingStateList: List<Flow<PagingData<HomeEntity.Data>>>) {
    var onSelect: (String) -> Unit = {

    }
    val scope: CoroutineScope = rememberCoroutineScope()

    Box(
        Modifier.fillMaxSize()
    ) {

        val pagerState = rememberPagerState(pageCount = {
            ids.size
        })

        var titleBarAlpha by rememberMutableStateOf { 0f }

        LaunchedEffect(Unit) {
            snapshotFlow { titleBarAlpha }.collect {
                BarUtils.setStatusBarLightMode(ActivityUtils.getTopActivity(), titleBarAlpha > 1F)
            }
        }

        var hashMap = mutableMapOf<String, Float>()

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect {
                onSelect(ids[it])
                titleBarAlpha = hashMap[ids[it]] ?: 0f
            }
        }


        if (pagingStateList.isNotEmpty()) {
            HorizontalPager(state = pagerState) { page ->
                HomeList(ids[page], pagingStateList[page].collectAsLazyPagingItems()) { id, alpha ->
                    hashMap[id] = alpha
                    if (page == pagerState.currentPage) {
                        titleBarAlpha = alpha
                    }
                }
            }
        }

        BoxWrapper {
            val bgColor by animateColorAsState(
                targetValue = lerp(Color.Black, Color.White, titleBarAlpha)
            )

            val selectColor by animateColorAsState(
                targetValue = lerp(Color.White, Color.Black, titleBarAlpha)
            )
            val unSelectColor by animateColorAsState(
                targetValue = lerp(Color.White, Color.Black, titleBarAlpha)
            )

            NewsTab(
                pagerState,
                ids,
                scope,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .statusBarsPadding(),
                selectColor = selectColor,
                unSelectColor = unSelectColor,
                onSelect
            )
        }
    }
}


@Composable
fun NewsTab(
    pagerState: PagerState,
    ids: List<String>,
    scope: CoroutineScope,
    modifier: Modifier,
    selectColor: Color,
    unSelectColor: Color,
    tabClick: (String) -> Unit,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpacerW(16)
        MyScrollableTabRow(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
                .background(Color.Transparent)
                .padding(bottom = 11.dp, top = 11.dp),
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                PagerTabIndicator(
                    tabPositions = tabPositions,
                    pagerState = pagerState,
                    with = 32.dp,
                    height = 3.dp
                )
            },
            backgroundColor = Color.Transparent,
            divider = {}) {
            ids.forEachIndexed { index, title ->
                PagerTab(
                    pagerState = pagerState,
                    index = index,
                    pageCount = ids.size,
                    text = title,
                    selectedContentColor = selectColor,
                    unselectedContentColor = unSelectColor,
                    modifier = Modifier
                        .padding(
                            bottom = 5.dp,
                            start = 8.dp,
                            end = 8.dp
                        )
                        .wrapContentWidth()
                        .click {
                            scope.launch {
                                tabClick(ids[index])
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .height(22.dp))
            }
        }

    }

}


@Composable
fun HomeList(
    id: String,
    lazyPagingItemsState: LazyPagingItems<HomeEntity.Data>,
    block: (String, Float) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    val targetHeight = BarUtils.getStatusBarHeight() + ConvertUtils.dp2px(50f)

    val firstVisibleScrollOffset by remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }

    val firstVisibleIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

    LaunchedEffect(Unit) {
        snapshotFlow { firstVisibleScrollOffset }.collect {
            if (firstVisibleIndex <= 1) {
                var titleBarAlpha = firstVisibleScrollOffset.toFloat() / targetHeight
                block(id, titleBarAlpha)
            }
        }
    }

    lazyPagingItemsState.PagingRefresh {
        it.PagingLazyColumn(lazyListState = lazyListState) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        1.5.dp,
                        androidx.compose.material.MaterialTheme.colors.secondary,
                        shape = CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.title,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Composable
fun OrderedTabsExample() {
    // 使用 remember 保持状态
    val tabs = remember {
        orderedStateMapOf(
            "Tabs DEF" to "Tabs ID DEF"
        )
    }

    (0..3).forEach {
        tabs.put("Tabs${it}", "Tabs ID ${it}")
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // 动态 Tab 行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 按顺序显示 tab
            for (key in tabs.orderedKeys) {
                val value = tabs[key]
                TabItem(title = key) {
                    // 点击删除
                    tabs.remove(key)
                }
            }

            // 添加新 tab 按钮示例
            Button(onClick = {
                tabs.addAt(0, "Add Tabs", "Add Tab ID")
            }) {
                Text("Add Tabs")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 内容展示
        for ((key, value) in tabs.orderedEntries()) {
            Text(text = "$key -> $value")
        }
    }
}

@Composable
fun TabItem(title: String, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Close, contentDescription = "Remove")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrderedTabsExample() {
    OrderedTabsExample()
}
