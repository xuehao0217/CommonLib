package com.xueh.commonlib.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.paging.compose.samples.ComposePaging
import com.xueh.comm_core.web.ComposeWebView
import com.xueh.comm_core.web.WebViewPage
import com.xueh.commonlib.ui.compose.BarPage
import com.xueh.commonlib.ui.compose.CarouselExamples
import com.xueh.commonlib.ui.compose.CommonTabPage
import com.xueh.commonlib.ui.compose.ComoposeViewModelPage
import com.xueh.commonlib.ui.compose.ConstraintPage
import com.xueh.commonlib.ui.compose.DialogPage
import com.xueh.commonlib.ui.compose.DemoListRow
import com.xueh.commonlib.ui.compose.LazyColumnPage
import com.xueh.commonlib.ui.compose.NavigateParams1View
import com.xueh.commonlib.ui.compose.NavigateParams2View
import com.xueh.commonlib.ui.compose.OrderedTabsExample
import com.xueh.commonlib.ui.compose.PageTwo
import com.xueh.commonlib.ui.compose.PagerPage
import com.xueh.commonlib.ui.compose.PermissionPageContent
import com.xueh.commonlib.ui.compose.RefreshLoadUse
import com.xueh.commonlib.ui.compose.TabPage
import com.xueh.commonlib.ui.compose.VisibilityChangedDemo
import com.xueh.commonlib.ui.compose.lazyVerticalGrid
import com.xueh.commonlib.ui.BaseComposeActivityApiDemoRoute
import com.xueh.commonlib.ui.compose.scrollableTabRow

private data class DemoMenuLine(val title: String, val key: NavKey)

private val demoMenuLines: List<DemoMenuLine> = listOf(
    DemoMenuLine("Dialog", DemoDialog),
    DemoMenuLine("公用 CommonTabPager", DemoCommonTabPager),
    DemoMenuLine("CarouselExamples", DemoCarousel),
    DemoMenuLine("ConstraintSet 使用", DemoConstraintSet),
    DemoMenuLine("scrollableTab 使用", DemoScrollableTabRow),
    DemoMenuLine("LazyVerticalGrid", DemoLazyVerticalGrid),
    DemoMenuLine("LazyColumn 列表示例", DemoLazyColumnPage),
    DemoMenuLine("ScrollableAppBar", DemoScrollableAppBar),
    DemoMenuLine("路由传参", DemoProfileRoute(name = "Kevin", age = 18)),
    DemoMenuLine("跳转互传参数", DemoNavigateParam1),
    DemoMenuLine("下拉加载使用", DemoRefreshLoad),
    DemoMenuLine("Compose 权限申请", DemoComposePermission),
    DemoMenuLine("WebView（CustomWebView + 进度条）", DemoWebView),
    DemoMenuLine("Compose WebView", DemoAccompanistWeb),
    DemoMenuLine("ComposeTab 分页加载", DemoComposeTab),
    DemoMenuLine("ComposePager", DemoComposePager),
    DemoMenuLine("ComposeViewModel", DemoComposeViewModel),
    DemoMenuLine("Compose Paging", DemoComposePaging),
    DemoMenuLine("VisibilityChanged", DemoVisibilityChanged),
    DemoMenuLine("OrderedTabs", DemoOrderedTabs),
    DemoMenuLine("BaseComposeActivity API 实验室", DemoBaseComposeActivityApi),
)

@Composable
fun DemoNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(DemoActionList)
    val navigateParamResult = remember { mutableStateOf("未返回数据") }
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        },
        transitionSpec = Nav3VerticalPushTransitionSpec,
        popTransitionSpec = Nav3VerticalPopTransitionSpec,
        predictivePopTransitionSpec = Nav3VerticalPredictivePopTransitionSpec,
        entryProvider = demoEntryProvider(backStack, navigateParamResult),
    )
}

private fun demoEntryProvider(
    backStack: NavBackStack<NavKey>,
    navigateParamResult: MutableState<String>,
) = entryProvider {
    entry<DemoActionList> {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    text = "全部示例",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
                )
            }
            itemsIndexed(
                items = demoMenuLines,
                key = { _, line -> line.title },
            ) { index, line ->
                DemoListRow(
                    title = line.title,
                    leadingIndex = index + 1,
                    onClick = { backStack.add(line.key) },
                )
            }
        }
    }
    entry<DemoRefreshLoad> { RefreshLoadUse() }
    entry<DemoConstraintSet> { ConstraintPage() }
    entry<DemoScrollableTabRow> { scrollableTabRow() }
    entry<DemoLazyVerticalGrid> { lazyVerticalGrid() }
    entry<DemoLazyColumnPage> { LazyColumnPage() }
    entry<DemoProfileRoute> { key ->
        PageTwo(name = key.name, age = key.age) {
            if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        }
    }
    entry<DemoScrollableAppBar> { BarPage() }
    entry<DemoNavigateParam1> {
        val result by navigateParamResult
        NavigateParams1View(
            resultText = result,
            onOpenSecond = { backStack.add(DemoNavigateParam2) },
        )
    }
    entry<DemoNavigateParam2> {
        NavigateParams2View(
            onDeliverResult = { navigateParamResult.value = "Hello world to you" },
            onPop = {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            },
        )
    }
    entry<DemoDialog> { DialogPage() }
    entry<DemoCommonTabPager> { CommonTabPage() }
    entry<DemoCarousel> { CarouselExamples() }
    entry<DemoComposePermission> { PermissionPageContent() }
    entry<DemoWebView> {
        WebViewPage {
            if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        }
    }
    entry<DemoAccompanistWeb> {
        Column(Modifier.fillMaxSize()) {
            ComposeWebView(url = "https://m.baidu.com")
        }
    }
    entry<DemoComposeTab> { TabPage() }
    entry<DemoComposePager> { PagerPage() }
    entry<DemoComposeViewModel> { ComoposeViewModelPage() }
    entry<DemoComposePaging> { ComposePaging() }
    entry<DemoVisibilityChanged> { VisibilityChangedDemo() }
    entry<DemoOrderedTabs> { OrderedTabsExample() }
    entry<DemoBaseComposeActivityApi> { BaseComposeActivityApiDemoRoute() }
}
