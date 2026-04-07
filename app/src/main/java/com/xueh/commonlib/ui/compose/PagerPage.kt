package com.xueh.commonlib.ui.compose

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.blankj.utilcode.util.LogUtils

@Preview
@Composable
fun PagerPage (){
    val pagerState = rememberPagerState(pageCount = {
        20
    })

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect {
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            Log.i("PagerPage", "Page changed to $it")
        }

        snapshotFlow { pagerState.targetPage }.collect {
            Log.i("PagerPage", "targetPage to $it")
        }

        snapshotFlow { pagerState.settledPage }.collect {
            Log.i("PagerPage", "settledPage to $it")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        DemoScreenIntro(
            text = "VerticalPager 纵向翻页；日志输出 currentPage / targetPage / settledPage。",
        )
        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            beyondViewportPageCount = 1,
        ) { page ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Log.i("PagerPage", "item--->${page}")
                Text(
                    text = "Page: $page",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}