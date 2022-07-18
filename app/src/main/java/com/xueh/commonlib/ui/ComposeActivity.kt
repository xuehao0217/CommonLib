package com.xueh.commonlib.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.MVVMComposeActivity
import com.xueh.comm_core.base.compose.theme.DarkColors
import com.xueh.comm_core.base.compose.theme.themeTypeState
import com.xueh.comm_core.weight.compose.*
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ComposeActivity : MVVMComposeActivity<ComposeViewModel>() {

    override fun getTitleText() = "我是标题"

    data class ItemData(var str: String, var router: String)

    @OptIn(ExperimentalMaterialApi::class)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setContent {
            contentRoot {
                NavHost()
            }
        }

        lifecycleScope.launch {
            delay(4000)
            themeTypeState.value = DarkColors()
        }
    }

    override fun initData() {
        viewModel.loadDsl()
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun NavHost() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = RouteConfig.ACTION_LIST) {
            composable(RouteConfig.ACTION_LIST) {
                var str = listOf(
                    ItemData("下拉加载使用", RouteConfig.REFRESHLOADUSE),
                    ItemData("ConstraintSet使用", RouteConfig.CONSTRAINTSET),
                    ItemData("scrollableTab使用", RouteConfig.SCROLLABLETABROW),
                    ItemData("lazyVerticalGrid使用", RouteConfig.LAZYVERTICALGRID),
                )
                LazyColumn() {
                    itemsIndexed(str) { _, item ->
                        itemView(item.str,false) {
                            navController.navigate(item.router)
                        }
                    }
                }
            }
            composable(RouteConfig.REFRESHLOADUSE) {
                refreshLoadUse()
            }
            composable(RouteConfig.CONSTRAINTSET) {
                ConstraintSet()
            }
            composable(RouteConfig.SCROLLABLETABROW) {
                scrollableTabRow()
            }
            composable(RouteConfig.LAZYVERTICALGRID) {
                lazyVerticalGrid()
            }
        }
    }


    @Composable
    fun refreshLoadUse() {
        val homeDatas = viewModel.getListDatas().collectAsLazyPagingItems()
        RefreshList(lazyPagingItems = homeDatas) {
            itemsIndexed(homeDatas) { _, item ->
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(Color.Blue)
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.5.dp, MaterialTheme.colors.secondary, shape = CircleShape),

                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item!!.title,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }

    @Composable
    fun ConstraintSet() {
        val orientation = remember { mutableStateOf(1) }
        ConstraintLayout(
            getConstraintLayout(orientation),
            Modifier
                .padding(16.dp, 0.dp, 16.dp, 0.dp)
                .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                .fillMaxWidth()
                .padding(12.dp, 12.dp, 12.dp, 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "效果图片",
                modifier = Modifier
                    .layoutId("imageRef")
                    .fillMaxWidth()
                    .clickable {
                        if (orientation.value == 0) {
                            orientation.value = 1
                        } else {
                            orientation.value = 0
                        }
                    }
                    .clip(shape = RoundedCornerShape(5)),
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = "泰迪犬其实是贵宾犬的一种。根据体型大小被分为四类，最受欢迎的是体型较小的品种：迷你贵宾犬和玩具贵宾犬。其中玩具贵宾犬是体型最小的一种，个性好动、欢快、非常机警、聪明、喜欢外出、性格脾气好、适应力强。贵宾犬不脱毛，是极好的宠物犬。如果红色玩具贵宾犬不剃胡须和嘴边的毛可以长成动漫画里面泰迪熊的模样，所以红色（褐色）玩具贵宾犬又叫“泰迪",
                modifier = Modifier.layoutId("titleRef"),
                fontSize = 18.sp,
                textAlign = TextAlign.Left,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (orientation.value == 0) Int.MAX_VALUE else 4,
            )
        }
    }


    private fun getConstraintLayout(orientation: MutableState<Int>): androidx.constraintlayout.compose.ConstraintSet {
        return ConstraintSet {
            val imageRef = createRefFor("imageRef")
            val titleRef = createRefFor("titleRef")

            if (orientation.value == 0) {
                constrain(imageRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                constrain(titleRef) {
                    start.linkTo(imageRef.start)
                    end.linkTo(imageRef.end)
                    top.linkTo(imageRef.bottom, 16.dp)
                    width = Dimension.fillToConstraints
                }
            } else {
                constrain(imageRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    width = Dimension.value(100.dp)
                    height = Dimension.value(100.dp)
                }
                constrain(titleRef) {
                    start.linkTo(imageRef.end, 8.dp)
                    top.linkTo(imageRef.top, 2.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(imageRef.bottom, 8.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            }
        }
    }


    @ExperimentalMaterialApi
    @Preview()
    @Composable
    fun scrollableTabRow() {
        val tabIndex = remember {
            mutableStateOf(0)
        }
        val tabDatas = ArrayList<String>().apply {
            add("第1tab")
            add("第2tab")
            add("第3tab")
            add("第4tab")
            add("第5tab")
            add("第6tab")
            add("第7tab")
            add("第8tab")
            add("第9tab")
        }
        ScrollableTabRow(
            selectedTabIndex = tabIndex.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            backgroundColor = Color.White,
            contentColor = Color.Black,
            edgePadding = 10.dp,
            divider = {
                TabRowDefaults.Divider(color = Color.Red)
            },
            indicator = {
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(it[tabIndex.value]),
                    color = Color.Blue,
                    height = 2.dp
                )
            },
            tabs = {
                tabDatas.forEachIndexed { index, s ->
                    leadingIconTabView(index, s, tabIndex)
                }
            })
    }


    @ExperimentalMaterialApi
    @Composable
    fun leadingIconTabView(index: Int, text: String, tabIndex: MutableState<Int>) {
        val interactionSource = remember {
            MutableInteractionSource()
        }
        val isPress = interactionSource.collectIsPressedAsState().value
        val imageVector = when (index) {
            0 -> Icons.Filled.Home
            1 -> Icons.Filled.Send
            2 -> Icons.Filled.Favorite
            3 -> Icons.Filled.Search
            4 -> Icons.Filled.Home
            5 -> Icons.Filled.Share
            6 -> Icons.Filled.Favorite
            7 -> Icons.Filled.Favorite
            8 -> Icons.Filled.Add
            else -> Icons.Filled.Person
        }
        LeadingIconTab(
            selected = index == tabIndex.value,
            onClick = {
                tabIndex.value = index
            },
            text = {
                Text(
                    text = text,
                    color = if (isPress || index == tabIndex.value) Color.Red else Color.Black
                )
            },
            icon = {
                Icon(
                    imageVector,
                    contentDescription = "icon图标",
                    tint = if (isPress || index == tabIndex.value) Color.Red else Color.Black
                )
            },
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .background(Color.White),
            enabled = true,
            interactionSource = interactionSource,
            selectedContentColor = Color.Red,
            unselectedContentColor = Color.Black
        )
    }


    @Preview()
    @Composable
    fun tabRow() {
        val tabIndex = remember {
            mutableStateOf(0)
        }
        val tabDatas = ArrayList<String>().apply {
            add("语文")
            add("数学")
            add("英语")
        }
        TabRow(
            selectedTabIndex = tabIndex.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            backgroundColor = Color.Green,
            contentColor = Color.Black,
            divider = {
                TabRowDefaults.Divider()
            },
            indicator = {
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(it[tabIndex.value]),
                    color = Color.Blue,
                    height = 2.dp
                )
            }
        ) {
            tabDatas.forEachIndexed { index, s ->
                tabView(index, s, tabIndex)
            }
        }
    }

    @Composable
    fun tabView(index: Int, text: String, tabIndex: MutableState<Int>) {
        val interactionSource = remember {
            MutableInteractionSource()
        }
        val isPress = interactionSource.collectIsPressedAsState().value
        Tab(
            selected = index == tabIndex.value,
            onClick = {
                tabIndex.value = index
            },
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight(),
            enabled = true,
            interactionSource = interactionSource,
            selectedContentColor = Color.Red,
            unselectedContentColor = Color.Black
        ) {
            Text(
                text = text,
                color = if (isPress || index == tabIndex.value) Color.Red else Color.Black
            )
        }
    }


    var imageUrl =
        "https://c-ssl.dtstatic.com/uploads/item/202105/29/20210529001057_aSeLB.thumb.1000_0.jpeg"

    @Preview(showBackground = true)
    @Preview
    @Composable
    fun constraint() {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
        ) {
            val (tv) = createRefs()
            Text(
                modifier = Modifier
                    .background(Color.Blue)
                    .constrainAs(tv) {
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        width = Dimension.fillToConstraints
                    }, text = "2222222"
            )
        }

    }


    @Preview()
    @Composable
    fun lazyVerticalGrid() {
        val scope = rememberCoroutineScope()
        var images = mutableListOf<String>()
        (0..60).forEach {
            images.add(imageUrl)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            val listState = rememberLazyGridState()
            LazyVerticalGrid(state = listState, columns = GridCells.Fixed(3), content = {
                items(images.size) { index ->
                    ImageLoadCompose(images[index])
                }
            })

            val showButton by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex > 0
                }
            }

            AnimatedVisibility(visible = showButton) {
                Column(modifier = Modifier.padding(bottom = 10.dp, end = 10.dp)) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Text(text = "回到顶部", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun LazyColumn() {
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
                    itemsIndexed(viewModel.bannerMutableState) { _, item ->
                        itemView(item.title) {
                            ToastUtils.showShort("点击了 ${item.title}")
                        }
                    }
//                    items(viewModel.bannerMutableState.size) { index ->
//
//                    }
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
    private fun itemView( item: String,showimg: Boolean = true, clickEvent: () -> Unit) {
        Surface(
//            shape = RoundedCornerShape(10.dp),
//            shadowElevation = 5.dp,
            modifier = Modifier
                .padding(all = 8.dp)
                .shadow(4.dp, shape = RoundedCornerShape(5))
        ) {
            Column() {
                if (showimg) {
                    ImageLoadCompose(imageUrl)
                }
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

