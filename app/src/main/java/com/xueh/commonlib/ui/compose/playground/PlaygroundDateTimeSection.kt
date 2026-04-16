package com.xueh.commonlib.ui.compose.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundDateTimeSection() {
    PlaygroundSectionTitle("DatePickerDialog / TimePickerDialog")
    PlaygroundSectionCaption("确认后可在下方看到当前选择的日期毫秒（演示用 SimpleDateFormat）。")
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()
    val timeState = rememberTimePickerState(is24Hour = true)
    var pickedMillis by remember { mutableStateOf<Long?>(null) }
    var timeLabel by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Button(onClick = { showDate = true }) { Text("选择日期") }
        Button(
            onClick = { showTime = true },
            modifier = Modifier.padding(start = 8.dp),
        ) { Text("选择时间") }
    }

    if (showDate) {
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { pickedMillis = it }
                        showDate = false
                    },
                ) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text("取消") }
            },
        ) {
            DatePicker(state = dateState)
        }
    }
    if (showTime) {
        TimePickerDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        timeLabel = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            timeState.hour,
                            timeState.minute,
                        )
                        showTime = false
                    },
                ) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showTime = false }) { Text("取消") }
            },
            title = { Text("选择时间") },
            content = { TimePicker(state = timeState) },
        )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        pickedMillis?.let { ms ->
            Text(
                text = "已选日期：${dateFormat.format(Date(ms))}（$ms）",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        timeLabel?.let { t ->
            Text(
                text = "已选时间：$t",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
