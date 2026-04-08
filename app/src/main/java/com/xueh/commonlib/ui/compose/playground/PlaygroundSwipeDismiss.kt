package com.xueh.commonlib.ui.compose.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private data class SwipeDemoItem(
    val id: Long,
    val title: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundSwipeDismissSection() {
    PlaygroundSectionCaption(
        "双向滑动删除；列表基于 snapshotStateList。使用 rememberSwipeToDismissBoxState + SwipeToDismissBox.onDismiss（AnchoredDraggable 路径，无 confirmValueChange）。",
    )
    val items = remember {
        mutableStateListOf(
            SwipeDemoItem(1L, "待办 Alpha"),
            SwipeDemoItem(2L, "待办 Beta"),
            SwipeDemoItem(3L, "待办 Gamma"),
            SwipeDemoItem(4L, "待办 Delta"),
        )
    }
    val scheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        items.forEach { item ->
            key(item.id) {
                val dismissState = rememberSwipeToDismissBoxState()
                val onDismissed = remember(item.id) {
                    { _: SwipeToDismissBoxValue ->
                        items.removeAll { it.id == item.id }
                        Unit
                    }
                }
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = true,
                    onDismiss = onDismissed,
                    backgroundContent = {
                        val color = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> scheme.errorContainer
                            SwipeToDismissBoxValue.StartToEnd -> scheme.tertiaryContainer
                            SwipeToDismissBoxValue.Settled -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 2.dp)
                                .background(color),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = scheme.onErrorContainer,
                                modifier = Modifier.padding(end = 24.dp),
                            )
                        }
                    },
                    content = {
                        ListItem(
                            headlineContent = { Text(item.title) },
                            supportingContent = { Text("左右滑删除该项") },
                        )
                        HorizontalDivider()
                    },
                )
            }
        }
        if (items.isEmpty()) {
            Text(
                "列表已空",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        Button(
            onClick = {
                items.clear()
                items.addAll(
                    listOf(
                        SwipeDemoItem(1L, "待办 Alpha"),
                        SwipeDemoItem(2L, "待办 Beta"),
                        SwipeDemoItem(3L, "待办 Gamma"),
                        SwipeDemoItem(4L, "待办 Delta"),
                    ),
                )
            },
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Text("重置列表")
        }
    }
}
