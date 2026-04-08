@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.xueh.commonlib.ui.compose.playground

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp

@Composable
fun PlaygroundSearchBarSection() {
    PlaygroundSectionTitle("SearchBar + SearchBarDefaults")
    PlaygroundSectionCaption(
        "展开后展示建议列表；选中一项会回填并收起。置于 verticalScroll 内时必须限制最大高度，否则 SearchBar 测量会得到非法 Constraints。",
    )
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val suggestions = remember { listOf("Compose", "Material3", "Navigation3", "Paging", "DataStore") }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { active = false },
                expanded = active,
                onExpandedChange = { active = it },
                placeholder = { Text("搜索演示") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (active) {
                        IconButton(onClick = {
                            if (query.isNotEmpty()) query = "" else active = false
                        }) {
                            Icon(Icons.Default.Close, "关闭")
                        }
                    }
                },
            )
        },
        expanded = active,
        onExpandedChange = { active = it },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            // verticalScroll 子项在竖直方向为无限大 max；SearchBar 内部会用 fixed 约束，必须封顶高度
            .heightIn(max = 400.dp)
            .semantics { traversalIndex = -1f },
    ) {
        suggestions.filter { it.contains(query, ignoreCase = true) }.forEach { s ->
            ListItem(
                headlineContent = { Text(s) },
                modifier = Modifier
                    .clickable {
                        query = s
                        active = false
                    }
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
fun PlaygroundChipSection() {
    PlaygroundSectionTitle("FilterChip / AssistChip / InputChip")
    PlaygroundSectionCaption("Filter 多选；Assist 可带图标；InputChip 常配合关闭或选中态。")
    var filterTags by remember { mutableStateOf(setOf<String>()) }
    val options = listOf("Kotlin", "Compose", "Android", "KMP")
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { tag ->
            FilterChip(
                selected = filterTags.contains(tag),
                onClick = {
                    filterTags = if (filterTags.contains(tag)) filterTags - tag else filterTags + tag
                },
                label = { Text(tag) },
            )
        }
    }
    Text(
        text = "已选：${if (filterTags.isEmpty()) "无" else filterTags.sorted().joinToString("，")}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
    )
    var assistHint by remember { mutableStateOf<String?>(null) }
    var inputSelected by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { assistHint = "AssistChip：建议型操作，与 Filter 的选中语义不同。" },
                label = { Text("Assist") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                    )
                },
                colors = AssistChipDefaults.assistChipColors(),
            )
            InputChip(
                selected = inputSelected,
                onClick = { inputSelected = !inputSelected },
                label = { Text("InputChip") },
            )
        }
        assistHint?.let { hint ->
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
