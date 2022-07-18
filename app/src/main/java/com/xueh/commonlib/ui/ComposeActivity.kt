package com.xueh.commonlib.ui

import androidx.compose.runtime.*
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.MVVMComposeActivity
import com.xueh.comm_core.base.compose.theme.DarkColors
import com.xueh.comm_core.base.compose.theme.themeTypeState
import com.xueh.comm_core.weight.compose.*
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.compose.*
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

//        lifecycleScope.launch {
//            delay(4000)
//            themeTypeState.value = DarkColors()
//        }
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
                    ItemData("LazyColumnPage", RouteConfig.LAZYCOLUMNPAGE),
                    ItemData("路由传参", RouteConfig.PARAMETER),
                )
                LazyColumn() {
                    itemsIndexed(str) { _, item ->
                        itemView(item.str, false) {
                            if (item.router==RouteConfig.PARAMETER){
                                navController.navigate("${RouteConfig.PARAMETER}/Kevin")
                            }else{
                                navController.navigate("${item.router}")
                            }
                        }
                    }
                }
            }
            composable(RouteConfig.REFRESHLOADUSE) {
                refreshLoadUse(viewModel)
            }
            composable(RouteConfig.CONSTRAINTSET) {
                ConstraintPage()
            }
            composable(RouteConfig.SCROLLABLETABROW) {
                scrollableTabRow()
            }
            composable(RouteConfig.LAZYVERTICALGRID) {
                lazyVerticalGrid()
            }
            composable(RouteConfig.LAZYCOLUMNPAGE) {
                LazyColumnPage(viewModel)
            }

            composable(
                "${RouteConfig.PARAMETER}/{${RouteConfig.name}}"
            ) {
                val name = it.arguments?.getString(RouteConfig.name)
                PageTwo(navController, name ?: "NULL")
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

}

