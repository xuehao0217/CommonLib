package com.xueh.commonlib.ui

import android.os.Build
import androidx.compose.runtime.*
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.base.compose.theme.*
import com.xueh.comm_core.weight.compose.CommonTitlePage
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.compose.*

class ComposeActivity : BaseComposeActivity() {
    override fun initView(savedInstanceState: Bundle?) {
        setContent {
            var showMenu by remember {
                mutableStateOf(false)
            }
            CommonTitlePage(this, title = "Compose", titleRightContent = {
                Row(Modifier.background(MaterialTheme.colorScheme.background)) {
                    androidx.compose.material3.IconButton(onClick = {
                        appThemeState.value = appThemeState
                            .value.copy(darkTheme = !appThemeState.value.darkTheme)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sleep),
                            contentDescription = "",
                            tint = if (appThemeState.value.darkTheme) Color.White else Color.Black
                        )
                    }

                    androidx.compose.material3.IconButton(onClick = {
                        showMenu = !showMenu
                    }) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "",
                            tint = if (appThemeState.value.darkTheme) Color.White else Color.Black
                        )
                    }
                }
            }) {
                NavHost()
                if (showMenu) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        PalletMenu(
                            modifier = Modifier.align(Alignment.TopEnd)) {
                            appThemeState.value =
                                appThemeState.value.copy(darkTheme = appThemeState.value.darkTheme,
                                    appThemeColorType = it)
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun NavHost() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = RouteConfig.ACTION_LIST) {
            composable(RouteConfig.ACTION_LIST) {
                ToastUtils.showShort("${navController.currentBackStackEntry?.destination}")

                var str = listOf(
                    ItemData("下拉加载使用", RouteConfig.REFRESHLOADUSE),
                    ItemData("ConstraintSet使用", RouteConfig.CONSTRAINTSET),
                    ItemData("scrollableTab使用", RouteConfig.SCROLLABLETABROW),
                    ItemData("lazyVerticalGrid使用", RouteConfig.LAZYVERTICALGRID),
                    ItemData("LazyColumnPage", RouteConfig.LAZYCOLUMNPAGE),
                    ItemData("路由传参", RouteConfig.PARAMETER),
                    ItemData("ScrollableAppBar", RouteConfig.SCROLLABLEAPPBAR),
                    ItemData("跳转互传参数", RouteConfig.navigate_param_transfer1),
                )
                LazyColumn() {

                    itemsIndexed(str) { _, item ->
                        itemView(item.str, false) {
                            if (item.router == RouteConfig.PARAMETER) {
                                navController.navigate("${RouteConfig.PARAMETER}/Kevin")
                            } else {
                                navController.navigate("${item.router}")
                            }
                        }
                    }
                }
            }
            composable(RouteConfig.REFRESHLOADUSE) {
                refreshLoadUse()
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
                LazyColumnPage()
            }
            composable(
                "${RouteConfig.PARAMETER}/{${RouteConfig.name}}" + "" +
                        "?${RouteConfig.age}={${RouteConfig.age}} ",
                arguments = listOf(
                    navArgument("age") {
                        type = NavType.IntType  //类型
                        defaultValue = 18  //默认值
//                        nullable = true //是否可空
                    }
                )
            ) {
                val name = it.arguments?.getString(RouteConfig.name)
                val age = it.arguments?.getInt(RouteConfig.age)
                PageTwo(navController, name ?: "NULL", age ?: 0)
            }
            composable(RouteConfig.SCROLLABLEAPPBAR) {
                BarPage()
            }

            composable(RouteConfig.navigate_param_transfer1) {
                NavigateParams1View(navController)
            }

            composable(RouteConfig.navigate_param_transfer2) {
                NavigateParams2View(navController)
            }
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

@Composable
fun MenuItem(color: Color, name: String, onPalletChange: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onPalletChange),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            tint = color,
            contentDescription = null
        )
        Text(text = name, modifier = Modifier.padding(8.dp))
    }
}


data class ItemData(var str: String, var router: String)