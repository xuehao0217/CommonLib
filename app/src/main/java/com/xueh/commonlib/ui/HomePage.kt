package com.xueh.commonlib.ui

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.compose.theme.AppThemeColorType
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.appThemeColorType
import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.base.compose.theme.blue500
import com.xueh.comm_core.base.compose.theme.green500
import com.xueh.comm_core.base.compose.theme.orange500
import com.xueh.comm_core.base.compose.theme.purple
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.samples.ComposePaging
import com.xueh.comm_core.web.WebViewPage
import com.xueh.comm_core.weight.compose.ImageLoadCompose
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.compose.BarPage
import com.xueh.commonlib.ui.compose.CarouselExamples
import com.xueh.commonlib.ui.compose.CommonTabPage
import com.xueh.commonlib.ui.compose.ComoposeViewModelPage
import com.xueh.commonlib.ui.compose.ConstraintPage
import com.xueh.commonlib.ui.compose.DialogPage
import com.xueh.commonlib.ui.compose.ItemView
import com.xueh.commonlib.ui.compose.LazyColumnPage
import com.xueh.commonlib.ui.compose.NavigateParams1View
import com.xueh.commonlib.ui.compose.NavigateParams2View
import com.xueh.commonlib.ui.compose.OrderedTabsExample
import com.xueh.commonlib.ui.compose.PageTwo
import com.xueh.commonlib.ui.compose.PagerPage
import com.xueh.commonlib.ui.compose.PermissionPageContent
import com.xueh.commonlib.ui.compose.PlaceholderPage
import com.xueh.commonlib.ui.compose.RefreshLoadUse
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.compose.TabPage
import com.xueh.commonlib.ui.compose.VisibilityChangedDemo
import com.xueh.commonlib.ui.compose.google.CustomPullRefreshSample
import com.xueh.commonlib.ui.compose.google.GoogleSamplePage
import com.xueh.commonlib.ui.compose.google.PullRefreshIndicatorTransformSample
import com.xueh.commonlib.ui.compose.google.PullRefreshSample
import com.xueh.commonlib.ui.compose.imageUrl
import com.xueh.commonlib.ui.compose.lazyVerticalGrid
import com.xueh.commonlib.ui.compose.scrollableTabRow

@Composable
fun HomePage() {
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // ---------------- Header ----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isDarkTheme = AppThemeType.isDark(themeType = appThemeType)
            IconButton(onClick = {
                appThemeType = if (isDarkTheme) AppThemeType.Light else AppThemeType.Dark
            }) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_sleep),
                    contentDescription = "切换主题",
                    tint = if (isDarkTheme) Color.White else Color.Black
                )
            }

            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "菜单",
                    tint = if (isDarkTheme) Color.White else Color.Black
                )
            }
        }

        // ---------------- Menu ----------------
        Box(modifier = Modifier.fillMaxWidth()) {
            if (showMenu) {
                PalletMenu(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onPalletChange = { appThemeColorType = it }
                )
            }
        }

        // ---------------- 内容区 ----------------
        // 这里可以放你页面的主内容，比如 LazyColumn、Pager 等
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            NavHost()
        }
    }
}

val str = listOf(
    ItemData("Dialog", RouteConfig.DialogPage),
    ItemData("公用CommonTabPager", RouteConfig.CommonTabPager),
    ItemData("CarouselExamples", RouteConfig.CarouselExamples),
    ItemData("ConstraintSet使用", RouteConfig.ConstraintSet),
    ItemData("scrollableTab使用", RouteConfig.scrollableTabRow),
    ItemData("路由传参", RouteConfig.Parameter),
    ItemData("跳转互传参数", RouteConfig.navigate_param_transfer1),
    ItemData("下拉加载使用", RouteConfig.refreshLoadUse),
    ItemData("Compose下权限申请", RouteConfig.ComposePermission),
    ItemData("Compose Placeholder", RouteConfig.Placeholder),
    ItemData("WebView 使用", RouteConfig.WebView),
    ItemData("GoogleSample", RouteConfig.GoogleSample),
    ItemData("ComposeLoadPage", RouteConfig.ComposeTab),
    ItemData("ComposePager", RouteConfig.ComposePager),
    ItemData("ComposeViewModel", RouteConfig.ComposeViewModelPage),
    ItemData("ComposeViewPaging", RouteConfig.ComposeViewPaging),
    ItemData("VisibilityChanged", RouteConfig.onVisibilityChanged),
    ItemData(
        "OrderedTabs",
        RouteConfig.OrderedTabs
    ), // ItemData("lazyVerticalGrid使用", RouteConfig.lazyVerticalGrid), // ItemData("LazyColumnPage", RouteConfig.LazyColumnPage), // ItemData("ScrollableAppBar", RouteConfig.ScrollableAppBar),
)

@Composable
fun NavHost() {
    val navController = rememberNavController()
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = RouteConfig.ActionList
    ) {
        composable(RouteConfig.ActionList) {
//                ToastUtils.showShort("${navController.currentBackStackEntry?.destination}")
            LazyColumn {
                itemsIndexed(str) { _, item ->
                    ItemView(item = item.str, showImg = false) {
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
        composable(RouteConfig.CarouselExamples) {
            CarouselExamples()
        }

        composable(RouteConfig.ComposePermission) {
            PermissionPageContent()
        }

        composable(RouteConfig.Placeholder) {
            PlaceholderPage()
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

        composable(RouteConfig.onVisibilityChanged) {
            VisibilityChangedDemo()
        }

        composable(RouteConfig.OrderedTabs) {
            OrderedTabsExample()
        }
    }
}

data class ItemData(var str: String, var router: String)

@Composable
fun PalletMenu(
    modifier: Modifier = Modifier,
    onPalletChange: (AppThemeColorType) -> Unit,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .animateContentSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        MenuItem(green500, "Green") {
            onPalletChange(
                AppThemeColorType.GREEN
            )
        }
        MenuItem(purple, "Purple") {
            onPalletChange(
                AppThemeColorType.PURPLE
            )
        }
        MenuItem(orange500, "Orange") {
            onPalletChange(
                AppThemeColorType.ORANGE
            )
        }
        MenuItem(
            blue500,
            "Blue"
        ) { onPalletChange(AppThemeColorType.BLUE) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MenuItem(
                dynamicLightColorScheme(LocalContext.current).primary,
                "Dynamic"
            ) {
                onPalletChange(AppThemeColorType.WALLPAPER)
            }
        }
    }
}

@Composable
fun MenuItem(color: Color, name: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(imageVector = Icons.Filled.Menu, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name)
    }
}


