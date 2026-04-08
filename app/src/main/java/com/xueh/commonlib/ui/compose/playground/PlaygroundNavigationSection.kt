package com.xueh.commonlib.ui.compose.playground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundNavigationRailSection() {
    val scheme = MaterialTheme.colorScheme
    var railIndex by remember { mutableIntStateOf(0) }
    PlaygroundSectionTitle("NavigationRail")
    PlaygroundSectionCaption("平板 / 横屏常见：左侧竖向目的地，右侧为主内容。")
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(156.dp),
    ) {
        NavigationRail {
            NavigationRailItem(
                selected = railIndex == 0,
                onClick = { railIndex = 0 },
                icon = { Icon(Icons.Outlined.Home, null) },
                label = { Text("主页") },
            )
            NavigationRailItem(
                selected = railIndex == 1,
                onClick = { railIndex = 1 },
                icon = { Icon(Icons.Outlined.Person, null) },
                label = { Text("我的") },
            )
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 8.dp),
            color = scheme.surfaceContainerLow,
            shape = MaterialTheme.shapes.medium,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Rail 选中: $railIndex",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundDismissibleDrawerSection(scope: CoroutineScope) {
    val scheme = MaterialTheme.colorScheme
    PlaygroundSectionTitle("DismissibleNavigationDrawer")
    PlaygroundSectionCaption("从边缘滑入 / 拖动手势关闭，与 Modal 抽屉的遮罩全屏不同。")
    val miniDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(228.dp),
    ) {
        DismissibleNavigationDrawer(
            drawerState = miniDrawer,
            drawerContent = {
                DismissibleDrawerSheet(modifier = Modifier.width(200.dp)) {
                    Text(
                        "抽屉内容",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        "向左滑主内容区可关闭",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                    )
                }
            },
            content = {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = scheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Button(onClick = { scope.launch { miniDrawer.open() } }) {
                            Text("打开 Dismissible 抽屉")
                        }
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundPermanentDrawerSection() {
    val scheme = MaterialTheme.colorScheme
    PlaygroundSectionTitle("PermanentNavigationDrawer")
    PlaygroundSectionCaption("大屏折叠 / 桌面：侧栏常驻，无 scrim；此处为固定高度示意。")
    PermanentNavigationDrawer(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(168.dp),
        drawerContent = {
            PermanentDrawerSheet(modifier = Modifier.width(168.dp)) {
                Spacer(Modifier.height(8.dp))
                Column(Modifier.padding(horizontal = 8.dp)) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Outlined.Folder, null) },
                        label = { Text("项目") },
                        selected = true,
                        onClick = { },
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Outlined.Star, null) },
                        label = { Text("收藏") },
                        selected = false,
                        onClick = { },
                    )
                }
            }
        },
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = scheme.surfaceContainerHighest,
            shape = MaterialTheme.shapes.medium,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "主内容列（无抽屉遮罩）",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                )
            }
        }
    }
}
