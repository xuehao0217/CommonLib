/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.paging.compose.samples

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.xueh.commonlib.navigation.Nav3VerticalPopTransitionSpec
import com.xueh.commonlib.navigation.Nav3VerticalPredictivePopTransitionSpec
import com.xueh.commonlib.navigation.Nav3VerticalPushTransitionSpec
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.serialization.Serializable
import androidx.paging.compose.itemKey
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
import com.xueh.comm_core.helper.compose.modifier
import com.xueh.comm_core.helper.compose.onScrollDirectionChanged
import com.xueh.comm_core.helper.compose.onScrollStopVisibleList
import com.xueh.comm_core.widget.PagingLazyColumn
import com.xueh.comm_core.widget.PagingRefreshColumn
import com.xueh.comm_core.widget.PagingVerticalGrid
import com.xueh.comm_core.widget.PagingVerticalPager
import com.xueh.commonlib.entity.HomeEntity
import com.xueh.commonlib.ui.compose.DemoArticleListCard
import com.xueh.commonlib.ui.compose.DemoListRow
import com.xueh.commonlib.ui.compose.DemoScreenIntro
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import kotlinx.coroutines.launch

@Serializable
private sealed interface PagingSampleKey : NavKey {
    @Serializable
    data object Menu : PagingSampleKey

    @Serializable
    data object PagingWithHorizontalPager : PagingSampleKey

    @Serializable
    data object PagingWithVerticalPager : PagingSampleKey

    @Serializable
    data object PagingWithLazyGrid : PagingSampleKey

    @Serializable
    data object PagingWithLazyList : PagingSampleKey

    @Serializable
    data object CustomRefresh : PagingSampleKey

    @Serializable
    data object RefreshPagingListSample : PagingSampleKey
}

@Composable
fun ComposePaging() {
    val backStack = rememberNavBackStack(PagingSampleKey.Menu)
    NavDisplay(
        backStack = backStack,
        onBack = dropUnlessResumed {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        },
        transitionSpec = Nav3VerticalPushTransitionSpec,
        popTransitionSpec = Nav3VerticalPopTransitionSpec,
        predictivePopTransitionSpec = Nav3VerticalPredictivePopTransitionSpec,
        entryProvider = entryProvider {
            entry<PagingSampleKey.Menu> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    DemoScreenIntro(
                        text = "Paging3 与 LazyGrid / Pager / 自定义 PullToRefresh 组合示例；点击进入子页。",
                    )
                    DemoListRow(
                        title = "PagingWithHorizontalPager",
                        onClick = dropUnlessResumed { backStack.add(PagingSampleKey.PagingWithHorizontalPager) },
                    )
                    DemoListRow(
                        title = "PagingWithVerticalPager",
                        onClick = dropUnlessResumed { backStack.add(PagingSampleKey.PagingWithVerticalPager) },
                    )
                    DemoListRow(
                        title = "PagingWithLazyGrid",
                        onClick = dropUnlessResumed { backStack.add(PagingSampleKey.PagingWithLazyGrid) },
                    )
                    DemoListRow(
                        title = "PagingWithLazyList",
                        onClick = dropUnlessResumed { backStack.add(PagingSampleKey.PagingWithLazyList) },
                    )
                    DemoListRow(
                        title = "CustomRefresh",
                        onClick = dropUnlessResumed { backStack.add(PagingSampleKey.CustomRefresh) },
                    )
                    DemoListRow(
                        title = "RefreshPagingListSample",
                        onClick = dropUnlessResumed { backStack.add(PagingSampleKey.RefreshPagingListSample) },
                    )
                }
            }
            entry<PagingSampleKey.PagingWithLazyList> { PagingWithLazyList() }
            entry<PagingSampleKey.PagingWithLazyGrid> { PagingWithLazyGrid() }
            entry<PagingSampleKey.PagingWithVerticalPager> { PagingWithVerticalPager() }
            entry<PagingSampleKey.PagingWithHorizontalPager> { PagingWithHorizontalPager() }
            entry<PagingSampleKey.CustomRefresh> { CustomRefreshSample() }
            entry<PagingSampleKey.RefreshPagingListSample> { RefreshPagingListSample() }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRefreshSample() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            DemoScreenIntro(
                text = "PagingRefreshColumn：Material 3 色刷新头 + 分页网格。",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                lazyPagingItems.PagingRefreshColumn(refreshHeader = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "刷新头 · secondaryContainer",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    }
                }) {
                    it.PagingVerticalGrid {
                        PagingItem(it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshPagingListSample() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            DemoScreenIntro(
                text = "列表 PullToRefresh + 滚动监听（停滑可见项 / 方向）。",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                lazyPagingItems.PagingRefreshColumn(refreshHeader = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "刷新头 · primaryContainer",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    }
                }) {
                    val state = rememberLazyListState()
                    state.onScrollStopVisibleList { vis ->
                        ToastUtils.showShort("onScrollStopVisibleList==${vis.toList()}")
                    }
                    state.onScrollDirectionChanged { up ->
                        LogUtils.iTag("AAA", "onScrollDirection==${up}")
                    }
                    lazyPagingItems.PagingLazyColumn(
                        lazyListState = state,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        key = lazyPagingItems.itemKey { it.id },
                    ) {
                        PagingItem(it)
                    }
                }
            }
        }
    }
}

@Composable
fun PagingWithHorizontalPager() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        val pagerState = rememberPagerState { lazyPagingItems.itemCount }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            DemoScreenIntro(
                text = "横向分页器展示分页项，固定页宽 200dp。",
            )
            HorizontalPager(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = pagerState,
                pageSize = PageSize.Fixed(200.dp),
                key = lazyPagingItems.itemKey { it.id },
            ) { index ->
                val item = lazyPagingItems[index]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PagingItem(item)
                }
            }
        }
    }
}

@Composable
fun PagingWithVerticalPager() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            DemoScreenIntro(
                text = "纵向 PagingVerticalPager：上下滑动翻页。",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                lazyPagingItems.PagingVerticalPager {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        PagingItem(it)
                    }
                }
            }
        }
    }
}

@Composable
fun PagingWithLazyGrid() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            DemoScreenIntro(
                text = "双列网格加载分页数据。",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                lazyPagingItems.PagingVerticalGrid {
                    PagingItem(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagingWithLazyList() {
    BaseComposeViewModel<ComposeViewModel> {
        val _modifier = it.getListDatas().modifier {
            it.id
        }

        val scope = rememberCoroutineScope()

        val lazyPagingItems = _modifier.flow.collectAsLazyPagingItems()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            DemoScreenIntro(
                text = "演示本地增删改分页数据：update / remove / addHeader / addFooter 等。",
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PagingDebugTonalButton(
                    text = "update 首条标题",
                    onClick = {
                        _modifier.update(
                            HomeEntity.Data(
                                title = "AAAAAAAAAAAAAAA",
                                id = lazyPagingItems.get(0)?.id ?: 0,
                            ),
                        )
                    },
                )
                PagingDebugTonalButton(
                    text = "remove 首条",
                    onClick = {
                        _modifier.remove(lazyPagingItems.get(0)?.id ?: 0)
                    },
                )
                PagingDebugTonalButton(
                    text = "removeAddHeader",
                    onClick = {
                        _modifier.removeAddHeader(1111)
                    },
                )
                PagingDebugTonalButton(
                    text = "addHeader",
                    onClick = {
                        scope.launch {
                            _modifier.addHeader(
                                HomeEntity.Data(
                                    title = "addHeader-DATA",
                                    id = 1111,
                                ),
                            )
                        }
                    },
                )
                PagingDebugTonalButton(
                    text = "addFooter",
                    onClick = {
                        scope.launch {
                            _modifier.addFooter(
                                HomeEntity.Data(
                                    title = "addFooter-DATA",
                                ),
                            )
                        }
                    },
                )
            }

            lazyPagingItems.PagingLazyColumn(
                lazyListState = rememberLazyListState(),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                key = lazyPagingItems.itemKey { it.id },
            ) {
                PagingItem(it)
            }
        }
    }
}

@Composable
private fun PagingDebugTonalButton(
    text: String,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun PagingItem(item: HomeEntity.Data?) {
    DemoArticleListCard(
        idText = "${item?.id ?: "—"}",
        title = item?.title ?: "",
    )
}
