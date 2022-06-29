package com.xueh.commonlib.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.coroutineScope
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.MVVMComposeActivity
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.weight.compose.ComposeTitleView
import com.xueh.comm_core.weight.compose.ImageCompose
import com.xueh.comm_core.weight.compose.ImageLoadCompose
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import com.xueh.commonlib.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class ComposeActivity : MVVMComposeActivity<ComposeViewModel>() {

    override fun getTitleText() = "我是标题"


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }

    override fun initData() {
        viewModel.loadDsl()
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        val bannerDatas by viewModel.bannerLiveData.observeAsState()
        contentRoot {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val scope = rememberCoroutineScope()
                val listState = rememberLazyListState()
                var isScroll by remember {
                    mutableStateOf(false)
                }

                if (listState.isScrollInProgress) {
                    DisposableEffect(Unit) {
                        isScroll = true
                        onDispose {
                            isScroll = false
                        }
                    }
                }

                val showButton by remember {
                    derivedStateOf { listState.firstVisibleItemIndex > 0 && !isScroll }
                }

                LazyColumn(state = listState) {
                    items(viewModel.bannerMutableState.size) { index ->
                        itemView(viewModel.bannerMutableState[index].title) {
                            ToastUtils.showShort("点击了 ${index}")
                        }
                    }
                }


                AnimatedVisibility(visible = showButton, modifier = Modifier.padding(15.dp)) {
                    FloatingActionButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Text(text = "置顶", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    @Composable
    private fun itemView(item: String, clickEvent: () -> Unit) {
        Surface(
//            shape = RoundedCornerShape(10.dp),
//            shadowElevation = 5.dp,
            modifier = Modifier
                .padding(all = 8.dp)
                .shadow(4.dp, shape = RoundedCornerShape(10))
        ) {
            Column() {
                ImageLoadCompose("https://pic-go-bed.oss-cn-beijing.aliyuncs.com/img/20220316151929.png")
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Blue)
                        .clickable(onClick = clickEvent)
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.5.dp, MaterialTheme.colors.secondary, shape = CircleShape),

                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }

        }

    }

}

