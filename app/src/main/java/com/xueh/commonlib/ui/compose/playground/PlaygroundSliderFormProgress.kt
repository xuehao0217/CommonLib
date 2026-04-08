package com.xueh.commonlib.ui.compose.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaygroundSliderSection() {
    PlaygroundSectionTitle("Slider / RangeSlider")
    PlaygroundSectionCaption("单值与区间；可与业务逻辑绑定做音量、筛选范围等。")
    var slider by remember { mutableFloatStateOf(0.35f) }
    var range by remember { mutableStateOf(0.2f..0.8f) }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Slider: ${"%.2f".format(slider)}", style = MaterialTheme.typography.bodySmall)
        Slider(
            value = slider,
            onValueChange = { slider = it },
            valueRange = 0f..1f,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Range: ${"%.2f".format(range.start)} — ${"%.2f".format(range.endInclusive)}",
            style = MaterialTheme.typography.bodySmall,
        )
        RangeSlider(
            value = range,
            onValueChange = { range = it },
            valueRange = 0f..1f,
        )
    }
}

@Composable
fun PlaygroundChoiceSection() {
    PlaygroundSectionTitle("Checkbox / RadioButton / Switch")
    PlaygroundSectionCaption("可聚焦表单控件；Radio 使用 selectableGroup 便于 TalkBack。")
    var checked by remember { mutableStateOf(true) }
    var agree by remember { mutableStateOf(false) }
    var radio by remember { mutableStateOf("标准") }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .selectableGroup(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = { checked = it })
            Text("订阅邮件", modifier = Modifier.padding(start = 8.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = agree, onCheckedChange = { agree = it })
            Text("同意用户协议", modifier = Modifier.padding(start = 8.dp))
        }
        Text("配送方式", style = MaterialTheme.typography.labelMedium)
        listOf("标准", "加急", "自提").forEach { opt ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = radio == opt, onClick = { radio = opt })
                Text(opt, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun PlaygroundProgressSection() {
    PlaygroundSectionTitle("LinearProgressIndicator / CircularProgressIndicator")
    PlaygroundSectionCaption("确定进度用 lambda progress；不定进度省略 progress 即可。")
    var linear by remember { mutableFloatStateOf(0.45f) }
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LinearProgressIndicator(
            progress = { linear },
            modifier = Modifier.fillMaxWidth(),
        )
        Slider(value = linear, onValueChange = { linear = it }, valueRange = 0f..1f)
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator()
            CircularProgressIndicator(strokeWidth = 2.dp)
        }
    }
}
