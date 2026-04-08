@file:OptIn(
    androidx.activity.ExperimentalActivityApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
)

package com.xueh.commonlib.ui.compose.playground

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xueh.commonlib.ui.compose.DemoScreenIntro
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Material 3 组件汇演：外层 Modal 抽屉 + 底栏 NavigationBar + Snackbar；内文拆至 playground 包各节。
 */
@Composable
fun Material3PlaygroundDemoScreen() {
    val scheme = MaterialTheme.colorScheme
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var bottomSelected by remember { mutableIntStateOf(0) }
    // 小于 0 表示未在预测返回中；0f～1f 为 BackEventCompat.progress，便于肉眼看到 Flow 是否在动
    var predictiveBackProgress by remember { mutableFloatStateOf(-1f) }

    PredictiveBackHandler(enabled = drawerState.isOpen) {
        try {
            it.collect { ev ->
                predictiveBackProgress = ev.progress.coerceIn(0f, 1f)
            }
            if (drawerState.isOpen) {
                scope.launch { drawerState.close() }
            }
        } catch (e: CancellationException) {
            throw e
        } finally {
            predictiveBackProgress = -1f
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ModalNavigationDrawer",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    label = { Text("首页") },
                    selected = bottomSelected == 0,
                    onClick = {
                        bottomSelected = 0
                        scope.launch { drawerState.close() }
                    },
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    label = { Text("设置") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "与顶栏菜单联动；Predictive Back 在打开时可尝试收尾关闭。",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        },
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        title = { Text("Material3 组件汇演") },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isOpen) drawerState.close() else drawerState.open()
                                    }
                                },
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "抽屉")
                            }
                        },
                    )
                    if (predictiveBackProgress >= 0f) {
                        LinearProgressIndicator(
                            progress = { predictiveBackProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp),
                            color = scheme.primary,
                            trackColor = scheme.surfaceVariant,
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    val items = listOf(
                        Triple("首页", Icons.Outlined.Home, 0),
                        Triple("探索", Icons.Outlined.Explore, 1),
                        Triple("库", Icons.Outlined.LibraryMusic, 2),
                    )
                    items.forEach { (label, icon, index) ->
                        NavigationBarItem(
                            selected = bottomSelected == index,
                            onClick = { bottomSelected = index },
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(label) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                item(key = "intro") {
                    DemoScreenIntro(
                        text = "导航：Modal 抽屉 + Bottom NavigationBar + NavigationRail + Permanent/Dismissible 抽屉示意；交互：SearchBar、Chip、日期时间、Slider、表单、进度、Snackbar、侧滑删除、动画与 SharedElement。主列表已 LazyColumn 化，仅组合可见区域附近的节。",
                    )
                    HorizontalDivider(color = scheme.outlineVariant)
                }
                item(key = "nav_rail") {
                    PlaygroundNavigationRailSection()
                    PlaygroundSectionDivider()
                }
                item(key = "perm_drawer") {
                    PlaygroundPermanentDrawerSection()
                    PlaygroundSectionDivider()
                }
                item(key = "dismiss_drawer") {
                    PlaygroundDismissibleDrawerSection(scope)
                    PlaygroundSectionDivider()
                }
                item(key = "search_chips") {
                    PlaygroundSearchBarSection()
                    PlaygroundSectionDivider()
                    PlaygroundChipSection()
                    PlaygroundSectionDivider()
                }
                item(key = "datetime_slider") {
                    PlaygroundDateTimeSection()
                    PlaygroundSectionDivider()
                    PlaygroundSliderSection()
                    PlaygroundSectionDivider()
                    PlaygroundChoiceSection()
                    PlaygroundSectionDivider()
                    PlaygroundProgressSection()
                    PlaygroundSectionDivider()
                }
                item(key = "snackbar") {
                    PlaygroundSnackbarDemo(snackbarHostState, scope)
                    PlaygroundSectionDivider()
                }
                item(key = "swipe_dismiss") {
                    PlaygroundSectionTitle("SwipeToDismissBox")
                    PlaygroundSwipeDismissSection()
                    PlaygroundSectionDivider()
                }
                item(key = "animation") {
                    PlaygroundSectionTitle("AnimatedVisibility / Crossfade / AnimatedContent")
                    PlaygroundAnimationSection()
                    PlaygroundSectionDivider()
                }
                item(key = "shared_transition") {
                    PlaygroundSectionTitle("Shared Element（scale 降级示意）")
                    PlaygroundSharedTransitionSection()
                }
                item(key = "predictive_back") {
                    PlaygroundSectionTitle("Predictive Back")
                    Text(
                        text = "如何感受：先打开左上角抽屉，再从屏幕左缘慢滑「返回」。顶栏下会出现进度条（BackEventCompat.progress），滑满松手后抽屉关闭；中途松手取消则进度条消失且抽屉保持打开。若系统未启用预测返回，可能只有「点按返回键」式结束，进度条几乎不动——可在系统设置里开启预测返回相关选项后重试。可临时注释 PredictiveBackHandler 对比：无拦截时返回多半直接退出本页而非先关抽屉。",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}
