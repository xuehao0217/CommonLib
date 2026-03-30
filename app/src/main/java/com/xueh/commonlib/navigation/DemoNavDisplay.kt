package com.xueh.commonlib.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.xueh.commonlib.ui.BaseComposeActivityApiDemoRoute
import com.xueh.commonlib.ui.compose.CarouselExamples
import com.xueh.commonlib.ui.compose.CommonTabPage
import com.xueh.commonlib.ui.compose.ConstraintPage
import com.xueh.commonlib.ui.compose.DialogPage
import com.xueh.commonlib.ui.compose.DemoListRow
import com.xueh.commonlib.ui.compose.NavigateParams1View
import com.xueh.commonlib.ui.compose.NavigateParams2View
import com.xueh.commonlib.ui.compose.OrderedTabsExample
import com.xueh.commonlib.ui.compose.PageTwo
import com.xueh.commonlib.ui.compose.PagerPage
import com.xueh.commonlib.ui.compose.PermissionPageContent
import com.xueh.commonlib.ui.compose.RefreshLoadUse
import com.xueh.commonlib.ui.compose.TabPage
import com.xueh.commonlib.ui.compose.VisibilityChangedDemo

/**
 * 主导航菜单顺序与可跳转 [NavKey] 的**唯一数据源**；增删示例只改此列表 + [DemoNavHostContext.RouteContent] 中对应分支。
 */
private val demoMenuEntries: List<Pair<String, NavKey>> = listOf(
    "Dialog" to DemoDialog,
    "公用 CommonTabPager" to DemoCommonTabPager,
    "CarouselExamples" to DemoCarousel,
    "ConstraintSet 使用" to DemoConstraintSet,
    "路由传参" to DemoProfileRoute(name = "Kevin", age = 18),
    "跳转互传参数" to DemoNavigateParam1,
    "下拉加载使用" to DemoRefreshLoad,
    "Compose 权限申请" to DemoComposePermission,
    "WebView（CustomWebView + 进度条）" to DemoWebView,
    "Compose WebView" to DemoAccompanistWeb,
    "ComposeTab 分页加载" to DemoComposeTab,
    "ComposePager" to DemoComposePager,
    "Compose Paging" to DemoComposePaging,
    "VisibilityChanged" to DemoVisibilityChanged,
    "OrderedTabs" to DemoOrderedTabs,
    "BaseComposeActivity API 实验室" to DemoBaseComposeActivityApi,
)

private class DemoNavHostContext(
    val backStack: NavBackStack<NavKey>,
    val navigateParamResult: MutableState<String>,
) {
    fun pop() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    @Composable
    fun RouteContent(routeKey: NavKey) {
        when (routeKey) {
            is DemoRefreshLoad -> RefreshLoadUse()
            is DemoConstraintSet -> ConstraintPage()
            is DemoProfileRoute ->
                PageTwo(name = routeKey.name, age = routeKey.age) { pop() }
            is DemoNavigateParam1 -> {
                val result by navigateParamResult
                NavigateParams1View(
                    resultText = result,
                    onOpenSecond = { backStack.add(DemoNavigateParam2) },
                )
            }
            is DemoNavigateParam2 ->
                NavigateParams2View(
                    onDeliverResult = { navigateParamResult.value = "Hello world to you" },
                    onPop = { pop() },
                )
            is DemoDialog -> DialogPage()
            is DemoCommonTabPager -> CommonTabPage()
            is DemoCarousel -> CarouselExamples()
            is DemoComposePermission -> PermissionPageContent()
            is DemoWebView ->
                WebViewPage {
                    pop()
                }
            is DemoAccompanistWeb -> AccompanistWebDemoPanel()
            is DemoComposeTab -> TabPage()
            is DemoComposePager -> PagerPage()
            is DemoComposePaging -> ComposePaging()
            is DemoVisibilityChanged -> VisibilityChangedDemo()
            is DemoOrderedTabs -> OrderedTabsExample()
            is DemoBaseComposeActivityApi -> BaseComposeActivityApiDemoRoute()
            else ->
                Text(
                    text = "未注册的路由: ${routeKey::class.simpleName}",
                    modifier = Modifier.padding(24.dp),
                )
        }
    }
}

/** Accompanist Web：可改 URL + 联网提示，降低外网/证书导致「Demo 坏了」的误判。 */
@Composable
private fun AccompanistWebDemoPanel() {
    var draftUrl by rememberSaveable { mutableStateOf("https://m.baidu.com") }
    var loadedUrl by rememberSaveable { mutableStateOf("https://m.baidu.com") }
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "需联网访问；若失败请更换 URL、检查代理/证书或稍后重试。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        OutlinedTextField(
            value = draftUrl,
            onValueChange = { draftUrl = it },
            label = { Text("URL") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        OutlinedButton(
            onClick = { loadedUrl = draftUrl },
            modifier = Modifier.padding(16.dp),
        ) {
            Text("加载此地址")
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            ComposeWebView(url = loadedUrl)
        }
    }
}

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
    val ctx = DemoNavHostContext(backStack, navigateParamResult)
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
                items = demoMenuEntries,
                key = { index, pair -> "${index}_${pair.second::class.simpleName}" },
            ) { index, (title, key) ->
                DemoListRow(
                    title = title,
                    leadingIndex = index + 1,
                    onClick = { backStack.add(key) },
                )
            }
        }
    }
    entry<DemoRefreshLoad> { ctx.RouteContent(DemoRefreshLoad) }
    entry<DemoConstraintSet> { ctx.RouteContent(DemoConstraintSet) }
    entry<DemoProfileRoute> { key -> ctx.RouteContent(key) }
    entry<DemoNavigateParam1> { ctx.RouteContent(DemoNavigateParam1) }
    entry<DemoNavigateParam2> { ctx.RouteContent(DemoNavigateParam2) }
    entry<DemoDialog> { ctx.RouteContent(DemoDialog) }
    entry<DemoCommonTabPager> { ctx.RouteContent(DemoCommonTabPager) }
    entry<DemoCarousel> { ctx.RouteContent(DemoCarousel) }
    entry<DemoComposePermission> { ctx.RouteContent(DemoComposePermission) }
    entry<DemoWebView> { ctx.RouteContent(DemoWebView) }
    entry<DemoAccompanistWeb> { ctx.RouteContent(DemoAccompanistWeb) }
    entry<DemoComposeTab> { ctx.RouteContent(DemoComposeTab) }
    entry<DemoComposePager> { ctx.RouteContent(DemoComposePager) }
    entry<DemoComposePaging> { ctx.RouteContent(DemoComposePaging) }
    entry<DemoVisibilityChanged> { ctx.RouteContent(DemoVisibilityChanged) }
    entry<DemoOrderedTabs> { ctx.RouteContent(DemoOrderedTabs) }
    entry<DemoBaseComposeActivityApi> { ctx.RouteContent(DemoBaseComposeActivityApi) }
}
