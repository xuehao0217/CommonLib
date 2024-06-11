package com.xueh.commonlib.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ToastUtils
import com.loren.component.view.composesmartrefresh.SmartSwipeRefresh
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import com.loren.component.view.composesmartrefresh.ThresholdScrollStrategy
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.xueh.comm_core.weight.compose.BoxText
import com.xueh.comm_core.weight.compose.CommonLazyColumnDatas
import com.xueh.comm_core.weight.compose.SpacerW
import com.xueh.comm_core.weight.compose.click
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeLoadPage (){
    var scope = rememberCoroutineScope()
    var refreshState = rememberSmartSwipeRefreshState()
    val scrollState = rememberLazyListState()

    refreshState.needFirstRefresh = false
    refreshState.enableRefresh = false

    var datas = remember {
        mutableStateListOf<String>()
    }
    (1..10).forEach {
        datas.add("${it}")
    }
    SmartSwipeRefresh(
        onLoadMore = {
            if (datas.size>30){
                ToastUtils.showLong("没有更多了")
                refreshState.loadMoreFlag= SmartSwipeStateFlag.SUCCESS
                return@SmartSwipeRefresh
            }
            scope.launch {
                delay(2000)
                (1..10).forEach {
                    datas.add("${it}")
                }
                refreshState.loadMoreFlag= SmartSwipeStateFlag.SUCCESS
            }

        },
        state = refreshState,
        footerIndicator = {
            LoadingItem()
        },
       ) {
        CommonLazyColumnDatas(
            datas = datas,
            state = scrollState,
            foodContent = { Box(modifier = Modifier.fillMaxWidth().height(200.dp))}
        ) {
            BoxText(
                text = "${it}", modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .click {
                        datas.remove(it)
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .animateItemPlacement(),
            )
        }
    }
}

@Preview
@Composable
fun LoadingItem() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(45.dp)) {
            CircularProgressIndicator(
                strokeWidth = 1.dp,
                modifier = Modifier.size(14.dp)
            )
            SpacerW(int = 8)
            Text(
                text = "Loading...",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF999999),
                )
            )
        }

    }

}
