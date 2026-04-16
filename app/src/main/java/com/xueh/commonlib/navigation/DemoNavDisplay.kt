package com.xueh.commonlib.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
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
import com.xueh.comm_core.web.AgentWebScaffold
import com.xueh.comm_core.web.ParkComposeWebViewScaffold
import com.xueh.commonlib.ui.BaseComposeActivityApiDemoRoute
import com.xueh.commonlib.ui.compose.CarouselExamples
import com.xueh.commonlib.ui.compose.CommonTabPage
import com.xueh.commonlib.ui.compose.ConstraintPage
import com.xueh.commonlib.ui.compose.DialogPage
import com.xueh.commonlib.ui.compose.AppShortcutsDemoScreen
import com.xueh.commonlib.ui.compose.DemoListRow
import com.xueh.commonlib.ui.compose.DemoScreenIntro
import com.xueh.commonlib.ui.compose.LoginComposeDemoScreen
import com.xueh.commonlib.ui.compose.Media3ExoPlayerDemoScreen
import com.xueh.commonlib.ui.compose.playground.Material3PlaygroundDemoScreen
import com.xueh.commonlib.ui.compose.NavigateParams1View
import com.xueh.commonlib.ui.compose.NavigateParams2View
import com.xueh.commonlib.ui.compose.OrderedTabsExample
import com.xueh.commonlib.ui.compose.PageTwo
import com.xueh.commonlib.ui.compose.PermissionPageContent
import com.xueh.commonlib.ui.compose.PhotoFilePickerDemoScreen
import com.xueh.commonlib.ui.compose.RefreshLoadUse
import com.xueh.commonlib.ui.compose.TabPage
import com.xueh.commonlib.ui.compose.VisibilityChangedDemo

/**
 * 主导航菜单顺序与可跳转 [NavKey] 的**唯一数据源**；增删示例只改此列表 + [DemoNavHostContext.RouteContent] 中对应分支。
 */
private val demoMenuEntries: List<Pair<String, NavKey>> = listOf(
    "桌面快捷方式（长按图标）" to DemoAppShortcutsInfo,
    "Dialog" to DemoDialog,
    "公用 CommonTabPager" to DemoCommonTabPager,
    "CarouselExamples" to DemoCarousel,
    "ConstraintSet 使用" to DemoConstraintSet,
    "路由传参" to DemoProfileRoute(name = "Kevin", age = 18),
    "跳转互传参数" to DemoNavigateParam1,
    "下拉加载使用" to DemoRefreshLoad,
    "登录 · Autofill / 省略号 / animateBounds" to DemoLoginCompose,
    "Material3 组件汇演（导航·抽屉·Search·动效）" to DemoMaterial3Playground,
    "Compose 权限申请" to DemoComposePermission,
    "相册 / 文件选择（Photo Picker · OpenDocument）" to DemoPhotoFilePicker,
    "Media3 ExoPlayer · Compose（ContentFrame + M3 控件）" to DemoMedia3ExoPlayer,
    "AgentWeb（内嵌）" to DemoAgentWeb,
    "Park Compose WebView" to DemoParkComposeWeb,
    "ComposeTab 分页加载" to DemoComposeTab,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            when (routeKey) {
            is DemoRefreshLoad -> RefreshLoadUse()
            is DemoLoginCompose -> LoginComposeDemoScreen()
            is DemoMaterial3Playground -> Material3PlaygroundDemoScreen()
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
            is DemoPhotoFilePicker -> PhotoFilePickerDemoScreen()
            is DemoMedia3ExoPlayer -> Media3ExoPlayerDemoScreen()
            is DemoAgentWeb -> DemoAgentWebPanel(onClose = { pop() })
            is DemoParkComposeWeb -> DemoParkComposeWebPanel()
            is DemoComposeTab -> TabPage()
            is DemoComposePaging -> ComposePaging()
            is DemoVisibilityChanged -> VisibilityChangedDemo()
            is DemoOrderedTabs -> OrderedTabsExample()
            is DemoBaseComposeActivityApi -> BaseComposeActivityApiDemoRoute()
            is DemoAppShortcutsInfo -> AppShortcutsDemoScreen()
            else ->
                Text(
                    text = "未注册的路由: ${routeKey::class.simpleName}",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

/** Nav3 内嵌 [AgentWebScaffold]：可改 URL，与 MainActivity 覆盖层 Web 共用同一套实现。 */
@Composable
private fun DemoAgentWebPanel(onClose: () -> Unit) {
    val activity = LocalActivity.current as? ComponentActivity
    var draftUrl by rememberSaveable { mutableStateOf("https://m.baidu.com") }
    var loadedUrl by rememberSaveable { mutableStateOf("https://m.baidu.com") }
    if (activity == null) {
        Text(
            text = "需要 ComponentActivity 环境",
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
        return
    }
    Column(Modifier.fillMaxSize()) {
        DemoScreenIntro(
            text = "需联网；修改 URL 后点「加载」切换页面。支持查询参数 hideTitle、showShare（与全屏 AgentWeb 一致）。",
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
            onClick = { loadedUrl = draftUrl.trim() },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            Text("加载此地址")
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            key(loadedUrl) {
                AgentWebScaffold(
                    activity = activity,
                    url = loadedUrl,
                    initialTitle = "",
                    onClose = onClose,
                )
            }
        }
    }
}

/**
 * [parkwoocheol/compose-webview](https://github.com/parkwoocheol/compose-webview)：
 * 使用 [ParkComposeWebViewScaffold]（common_core 封装）+ 可编辑 URL。
 */
@Composable
private fun DemoParkComposeWebPanel() {
    var draftUrl by rememberSaveable { mutableStateOf("https://m.baidu.com") }
    var loadedUrl by rememberSaveable { mutableStateOf("https://m.baidu.com") }
    Column(Modifier.fillMaxSize()) {
        DemoScreenIntro(
            text = "需联网。此为 Jetpack Compose 用 WebView 库（与 AgentWeb 不同栈），封装见 common_core ParkComposeWebViewScaffold。",
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
            onClick = { loadedUrl = draftUrl.trim() },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            Text("加载此地址")
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            ParkComposeWebViewScaffold(url = loadedUrl)
        }
    }
}

@Composable
fun DemoNavDisplay(
    modifier: Modifier = Modifier,
    launcherShortcutRequest: NavKey? = null,
    onLauncherShortcutConsumed: () -> Unit = {},
) {
    val backStack = rememberNavBackStack(DemoActionList)
    val navigateParamResult = remember { mutableStateOf("未返回数据") }
    LaunchedEffect(launcherShortcutRequest) {
        val dest = launcherShortcutRequest ?: return@LaunchedEffect
        if (backStack.isNotEmpty() && backStack.last() == dest) {
            onLauncherShortcutConsumed()
            return@LaunchedEffect
        }
        while (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
        backStack.add(dest)
        onLauncherShortcutConsumed()
    }
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
        var menuFilter by rememberSaveable { mutableStateOf("") }
        val filteredEntries = remember(menuFilter) {
            val q = menuFilter.trim()
            if (q.isEmpty()) {
                demoMenuEntries
            } else {
                demoMenuEntries.filter { (title, _) ->
                    title.contains(q, ignoreCase = true)
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                Text(
                    text = "全部示例",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
                )
            }
            item {
                DemoScreenIntro(
                    text = "以下为 CommonLib 功能演示入口；与 Material 3 色板、导航 3 配套。",
                )
            }
            item {
                OutlinedTextField(
                    value = menuFilter,
                    onValueChange = { menuFilter = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    singleLine = true,
                    label = { Text("筛选标题") },
                    placeholder = { Text("输入关键字，例如 Dialog、Paging") },
                )
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            if (filteredEntries.isEmpty()) {
                item(key = "empty_filter") {
                    Text(
                        text = "无匹配示例，请换关键字或清空筛选。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }
            } else {
                itemsIndexed(
                    items = filteredEntries,
                    key = { _, pair -> pair.first },
                ) { index, (title, key) ->
                    DemoListRow(
                        title = title,
                        leadingIndex = index + 1,
                        onClick = { backStack.add(key) },
                    )
                }
            }
        }
    }
    entry<DemoRefreshLoad> { ctx.RouteContent(DemoRefreshLoad) }
    entry<DemoLoginCompose> { ctx.RouteContent(DemoLoginCompose) }
    entry<DemoMaterial3Playground> { ctx.RouteContent(DemoMaterial3Playground) }
    entry<DemoConstraintSet> { ctx.RouteContent(DemoConstraintSet) }
    entry<DemoProfileRoute> { key -> ctx.RouteContent(key) }
    entry<DemoNavigateParam1> { ctx.RouteContent(DemoNavigateParam1) }
    entry<DemoNavigateParam2> { ctx.RouteContent(DemoNavigateParam2) }
    entry<DemoDialog> { ctx.RouteContent(DemoDialog) }
    entry<DemoCommonTabPager> { ctx.RouteContent(DemoCommonTabPager) }
    entry<DemoCarousel> { ctx.RouteContent(DemoCarousel) }
    entry<DemoComposePermission> { ctx.RouteContent(DemoComposePermission) }
    entry<DemoPhotoFilePicker> { ctx.RouteContent(DemoPhotoFilePicker) }
    entry<DemoMedia3ExoPlayer> { ctx.RouteContent(DemoMedia3ExoPlayer) }
    entry<DemoAgentWeb> { ctx.RouteContent(DemoAgentWeb) }
    entry<DemoParkComposeWeb> { ctx.RouteContent(DemoParkComposeWeb) }
    entry<DemoComposeTab> { ctx.RouteContent(DemoComposeTab) }
    entry<DemoComposePaging> { ctx.RouteContent(DemoComposePaging) }
    entry<DemoVisibilityChanged> { ctx.RouteContent(DemoVisibilityChanged) }
    entry<DemoOrderedTabs> { ctx.RouteContent(DemoOrderedTabs) }
    entry<DemoBaseComposeActivityApi> { ctx.RouteContent(DemoBaseComposeActivityApi) }
    entry<DemoAppShortcutsInfo> { ctx.RouteContent(DemoAppShortcutsInfo) }
}
