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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
import com.xueh.comm_core.weight.compose.PagingBaseBox
import com.xueh.comm_core.weight.compose.PagingVerticalGrid
import com.xueh.comm_core.weight.compose.PagingVerticalPager
import com.xueh.commonlib.entity.HomeEntity
import com.xueh.commonlib.ui.compose.ItemView
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel

@Composable
fun ComposePaging() {
    val PagingWithHorizontalPager = "PagingWithHorizontalPager"
    val PagingWithVerticalPager = "PagingWithVerticalPager"
    val PagingWithLazyGrid = "PagingWithLazyGrid"
    val PagingWithLazyList = "PagingWithLazyList"

    val PagingList = "PagingList"
    val navController = rememberNavController()
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = PagingList
    ) {
        composable(PagingList) {
            Column {
                ItemView(PagingWithHorizontalPager, false) {
                    navController.navigate(PagingWithHorizontalPager)
                }
                ItemView(PagingWithVerticalPager, false) {
                    navController.navigate(PagingWithVerticalPager)
                }
                ItemView(PagingWithLazyGrid, false) {
                    navController.navigate(PagingWithLazyGrid)
                }
                ItemView(PagingWithLazyList, false) {
                    navController.navigate(PagingWithLazyList)
                }
            }
        }

        composable(PagingWithLazyList) {
            PagingWithLazyList()
        }

        composable(PagingWithLazyGrid) {
            PagingWithLazyGrid()
        }

        composable(PagingWithVerticalPager) {
            PagingWithVerticalPager()
        }

        composable(PagingWithHorizontalPager) {
            PagingWithHorizontalPager()
        }
    }
}

@Composable
fun PagingWithHorizontalPager() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        val pagerState = rememberPagerState { lazyPagingItems.itemCount }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            pageSize = PageSize.Fixed(200.dp),
            key = lazyPagingItems.itemKey { it.id }
        ) { index ->
            val item = lazyPagingItems[index]
            PagingItem(item)
        }
    }

}

@Composable
fun PagingWithVerticalPager() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        val pagerState = rememberPagerState { lazyPagingItems.itemCount }
        PagingVerticalPager(lazyPagingItems = lazyPagingItems, pagerState) {
            PagingItem(it)
        }

    }
}

@Composable
fun PagingWithLazyGrid() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()
        PagingVerticalGrid(lazyPagingItems = lazyPagingItems) {
            PagingItem(it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagingWithLazyList() {
    BaseComposeViewModel<ComposeViewModel> {
        val lazyPagingItems = it.getListDatas().collectAsLazyPagingItems()

        LazyColumn {
            stickyHeader(
                key = "Header",
                contentType = "My Header",
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .background(Color.Red)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Header", fontSize = 32.sp)
                }
            }
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id },
                contentType = lazyPagingItems.itemContentType { "MyPagingItems" }
            ) { index ->
                val item = lazyPagingItems[index]
                PagingItem(item)
            }
        }
    }
}

@Composable
private fun PagingItem(item: HomeEntity.Data?) {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .clip(CircleShape)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .height(50.dp)
            .border(1.5.dp, MaterialTheme.colors.secondary, shape = CircleShape),

        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item?.title ?: "",
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}
