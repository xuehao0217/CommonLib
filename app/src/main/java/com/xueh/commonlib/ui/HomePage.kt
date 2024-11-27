package com.xueh.commonlib.ui

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.samples.ComposePaging
import com.blankj.utilcode.util.ToastUtils
import com.lt.compose_views.compose_pager.ComposePagerScope
import com.lt.compose_views.nav.NavContent
import com.xueh.comm_core.base.compose.theme.AppThemeColorType
import com.xueh.comm_core.base.compose.theme.appThemeState
import com.xueh.comm_core.base.compose.theme.blue500
import com.xueh.comm_core.base.compose.theme.green500
import com.xueh.comm_core.base.compose.theme.orange500
import com.xueh.comm_core.base.compose.theme.purple
import com.xueh.comm_core.web.WebViewPage
import com.xueh.comm_core.weight.compose.CommonTitlePage
import com.xueh.commonlib.ui.compose.ConstraintPage
import com.xueh.commonlib.ui.compose.NavPageWeigetPage
import com.xueh.commonlib.ui.compose.PermissionPageContent
import com.xueh.commonlib.ui.compose.PlaceholderPage
import com.xueh.commonlib.ui.compose.RefreshLoadUse
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.compose.google.GoogleSamplePage
import com.xueh.comm_core.base.compose.theme.appThemeState
import com.xueh.comm_core.base.compose.theme.blue500
import com.xueh.comm_core.base.compose.theme.green500
import com.xueh.comm_core.base.compose.theme.orange500
import com.xueh.comm_core.base.compose.theme.purple
import com.xueh.comm_core.web.WebViewPage
import com.xueh.comm_core.weight.compose.CommonTitlePage
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.compose.BarPage
import com.xueh.commonlib.ui.compose.CommonLazyColumnDatasPage
import com.xueh.commonlib.ui.compose.CommonPaging
import com.xueh.commonlib.ui.compose.CommonRefreshColumnData
import com.xueh.commonlib.ui.compose.CommonTabPage
import com.xueh.commonlib.ui.compose.ComoposeViewModelPage
import com.xueh.commonlib.ui.compose.ConstraintPage
import com.xueh.commonlib.ui.compose.DialogPage
import com.xueh.commonlib.ui.compose.ItemView
import com.xueh.commonlib.ui.compose.LazyColumnPage
import com.xueh.commonlib.ui.compose.NavPageWeigetPage
import com.xueh.commonlib.ui.compose.NavigateParams1View
import com.xueh.commonlib.ui.compose.NavigateParams2View
import com.xueh.commonlib.ui.compose.PageTwo
import com.xueh.commonlib.ui.compose.PagerPage
import com.xueh.commonlib.ui.compose.PermissionPageContent
import com.xueh.commonlib.ui.compose.PlaceholderPage
import com.xueh.commonlib.ui.compose.RefreshLoadUse
import com.xueh.commonlib.ui.compose.RouteConfig.ComposeViewPaging
import com.xueh.commonlib.ui.compose.TabPage
import com.xueh.commonlib.ui.compose.google.CustomPullRefreshSample
import com.xueh.commonlib.ui.compose.google.GoogleSamplePage
import com.xueh.commonlib.ui.compose.google.PullRefreshIndicatorTransformSample
import com.xueh.commonlib.ui.compose.google.PullRefreshSample
import com.xueh.commonlib.ui.compose.lazyVerticalGrid
import com.xueh.commonlib.ui.compose.scrollableTabRow

class HomePage : NavContent {
    override val route: String="HomePage"

    @Composable
    override fun Content(scope: ComposePagerScope) {
        var showMenu by remember {
            mutableStateOf(false)
        }
        CommonTitlePage(title = "Compose", showBackIcon = false, titleRightContent = {
            Row(Modifier.background(MaterialTheme.colorScheme.background)) {
                IconButton(onClick = {
                    appThemeState = appThemeState.copy(darkTheme = !appThemeState.darkTheme)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sleep),
                        contentDescription = "",
                        tint = if (appThemeState.darkTheme) Color.White else Color.Black
                    )
                }

                IconButton(onClick = {
                    showMenu = !showMenu
                }) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = "",
                        tint = if (appThemeState.darkTheme) Color.White else Color.Black
                    )
                }
            }
        }) {
            NavHost()
            if (showMenu) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    PalletMenu(
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        appThemeState = appThemeState.copy(
                            darkTheme = appThemeState.darkTheme, appThemeColorType = it
                        )
                    }
                }
            }
        }
    }



    @Composable
    fun NavHost() {
        val navController = rememberNavController()
        androidx.navigation.compose.NavHost(
            navController = navController,
            startDestination = RouteConfig.ActionList
        ) {
            composable(RouteConfig.ActionList) {
                ToastUtils.showShort("${navController.currentBackStackEntry?.destination}")

                var str = listOf(
                    ItemData("Dialog", RouteConfig.DialogPage),
                    ItemData("公用CommonTabPager", RouteConfig.CommonTabPager),
                    ItemData("公用CommonLazyColumnDatasPage", RouteConfig.CommonLazyColumnDatas),
                    ItemData("公用CommonRefreshColumnData", RouteConfig.CommonRefreshColumnData),
                    ItemData("公用CommonPagingPage", RouteConfig.CommonPaging),
                    ItemData("ConstraintSet使用", RouteConfig.ConstraintSet),
                    ItemData("scrollableTab使用", RouteConfig.scrollableTabRow),
                    ItemData("路由传参", RouteConfig.Parameter),
                    ItemData("跳转互传参数", RouteConfig.navigate_param_transfer1),
                    ItemData("下拉加载使用", RouteConfig.refreshLoadUse),
                    ItemData("Compose下权限申请", RouteConfig.ComposePermission),
                    ItemData("Compose Placeholder", RouteConfig.Placeholder),
                    ItemData("NavPageWeiget 使用", RouteConfig.NavPageWeiget),
                    ItemData("WebView 使用", RouteConfig.WebView),
                    ItemData("GoogleSample", RouteConfig.GoogleSample),

                    ItemData("ComposeLoadPage", RouteConfig.ComposeTab),
                    ItemData("ComposePager", RouteConfig.ComposePager),
                    ItemData("ComposeViewModel", RouteConfig.ComposeViewModelPage),
                    ItemData("ComposeViewPaging", RouteConfig.ComposeViewPaging),
//                    ItemData("lazyVerticalGrid使用", RouteConfig.lazyVerticalGrid),
//                    ItemData("LazyColumnPage", RouteConfig.LazyColumnPage),
//                    ItemData("ScrollableAppBar", RouteConfig.ScrollableAppBar),
                )
                LazyColumn {
                    itemsIndexed(str) { _, item ->
                        ItemView(item.str, false) {
                            if (item.router == RouteConfig.Parameter) {
                                navController.navigate("${RouteConfig.Parameter}/Kevin")
                            } else {
                                navController.navigate("${item.router}")
                            }
                        }
                    }
                }
            }
            composable(RouteConfig.refreshLoadUse) {
                RefreshLoadUse()
            }
            composable(RouteConfig.ConstraintSet) {
                ConstraintPage()
            }
            composable(RouteConfig.scrollableTabRow) {
                scrollableTabRow()
            }
            composable(RouteConfig.lazyVerticalGrid) {
                lazyVerticalGrid()
            }
            composable(RouteConfig.LazyColumnPage) {
                LazyColumnPage()
            }
            composable(
                "${RouteConfig.Parameter}/{${RouteConfig.name}}" + "" + "?${RouteConfig.age}={${RouteConfig.age}} ",
                arguments = listOf(navArgument("age") {
                    type = NavType.IntType  //类型
                    defaultValue = 18  //默认值
//                        nullable = true //是否可空
                })
            ) {
                val name = it.arguments?.getString(RouteConfig.name)
                val age = it.arguments?.getInt(RouteConfig.age)
                PageTwo(navController, name ?: "NULL", age ?: 0)
            }
            composable(RouteConfig.ScrollableAppBar) {
                BarPage()
            }

            composable(RouteConfig.navigate_param_transfer1) {
                NavigateParams1View(navController)
            }

            composable(RouteConfig.navigate_param_transfer2) {
                NavigateParams2View(navController)
            }
            composable(RouteConfig.DialogPage) {
                DialogPage()
            }
            composable(RouteConfig.CommonTabPager) {
                CommonTabPage()
            }
            composable(RouteConfig.CommonLazyColumnDatas) {
                CommonLazyColumnDatasPage()
            }
            composable(RouteConfig.CommonRefreshColumnData) {
                CommonRefreshColumnData()
            }

            composable(RouteConfig.CommonPaging) {
                CommonPaging()
            }

            composable(RouteConfig.ComposePermission) {
                PermissionPageContent()
            }

            composable(RouteConfig.Placeholder) {
                PlaceholderPage()
            }
            composable(RouteConfig.NavPageWeiget) {
                NavPageWeigetPage()
            }
            composable(RouteConfig.WebView) {
                WebViewPage {
                    navController.popBackStack()
                }
            }
            composable(RouteConfig.GoogleSample) {
                GoogleSamplePage(navController)
            }

            composable(RouteConfig.PullRefreshSample) {
                PullRefreshSample()
            }

            composable(RouteConfig.CustomPullRefreshSample) {
                CustomPullRefreshSample()
            }
            composable(RouteConfig.PullRefreshIndicatorTransformSample) {
                PullRefreshIndicatorTransformSample()
            }
            composable(RouteConfig.ComposeTab) {
                TabPage()
            }

            composable(RouteConfig.ComposePager) {
                PagerPage()
            }

            composable(RouteConfig.ComposeViewModelPage) {
                ComoposeViewModelPage()
            }

            composable(RouteConfig.ComposeViewPaging) {
                ComposePaging()
            }
        }
    }


    @Composable
    fun PalletMenu(
        modifier: Modifier,
        onPalletChange: (AppThemeColorType) -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .animateContentSize(),
            ) {
                MenuItem(green500, "Green") {
                    onPalletChange.invoke(AppThemeColorType.GREEN)
                }
                MenuItem(purple, "Purple") {
                    onPalletChange.invoke(AppThemeColorType.PURPLE)
                }
                MenuItem(orange500, "Orange") {
                    onPalletChange.invoke(AppThemeColorType.ORANGE)
                }
                MenuItem(blue500, "Blue") {
                    onPalletChange.invoke(AppThemeColorType.BLUE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MenuItem(dynamicLightColorScheme(LocalContext.current).primary, "Dynamic") {
                        onPalletChange.invoke(AppThemeColorType.WALLPAPER)
                    }
                }
            }
        }
    }

    data class ItemData(var str: String, var router: String)
}


@Composable
fun MenuItem(color: Color, name: String, onPalletChange: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onPalletChange), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Menu, tint = color, contentDescription = null
        )
        Text(text = name, modifier = Modifier.padding(8.dp))
    }
}