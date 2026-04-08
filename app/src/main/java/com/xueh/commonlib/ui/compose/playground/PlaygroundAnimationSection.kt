package com.xueh.commonlib.ui.compose.playground

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaygroundAnimationSection() {
    PlaygroundSectionCaption("切换 Tab 比较 enter/exit；AnimatedContent 适合多状态互斥 UI。")
    var mode by remember { mutableIntStateOf(0) }
    var vis by remember { mutableStateOf(true) }
    var crossTarget by remember { mutableStateOf(false) }
    var animKey by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("Visibility") })
        FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("Crossfade") })
        FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text("AnimatedContent") })
    }
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(148.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (mode) {
                0 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { vis = !vis }) { Text("切换显隐") }
                        AnimatedVisibility(
                            visible = vis,
                            enter = fadeIn(tween(220)) + expandVertically(),
                            exit = fadeOut(tween(220)) + shrinkVertically(),
                        ) {
                            Text(
                                "AnimatedVisibility + expand/shrink",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
                1 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { crossTarget = !crossTarget }) { Text("切换 Crossfade") }
                        Crossfade(targetState = crossTarget, label = "cf") { on ->
                            Text(
                                if (on) "Crossfade ON" else "Crossfade OFF",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { animKey = (animKey + 1) % 3 }) { Text("下一步") }
                        AnimatedContent(
                            targetState = animKey,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(240)) togetherWith
                                    fadeOut(animationSpec = tween(240))
                            },
                            label = "ac",
                        ) { target ->
                            Text(
                                when (target) {
                                    0 -> "步骤 ①"
                                    1 -> "步骤 ②"
                                    else -> "步骤 ③"
                                },
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}
