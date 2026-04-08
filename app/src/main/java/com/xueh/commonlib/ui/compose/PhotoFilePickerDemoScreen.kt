package com.xueh.commonlib.ui.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * 相册与文件选择：`PickVisualMedia` / `PickMultipleVisualMedia` 走系统 Photo Picker（低版本会退回文档选择器）；
 * `OpenDocument` 用于通用可读文件，在 MIME 数组中声明类型。
 */
@Composable
fun PhotoFilePickerDemoScreen() {
    val context = LocalContext.current
    var singleImageUri by remember { mutableStateOf<Uri?>(null) }
    var multiImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var documentUri by remember { mutableStateOf<Uri?>(null) }
    var lastMime by remember { mutableStateOf<String?>(null) }

    val pickSingleImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        singleImageUri = uri
    }

    val pickMultipleImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
    ) { uris ->
        multiImageUris = uris
    }

    val openDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        documentUri = uri
        lastMime = uri?.let { context.contentResolver.getType(it) }
    }

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        DemoScreenIntro(
            text = "单图/多图使用 Photo Picker 契约，无需存储读写权限即可读取返回的 Uri。下方「文件」使用 OpenDocument，可按 MIME 多选类型；非图片仅展示 Uri 与系统解析的 MIME。",
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("单张图片", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        FilledTonalButton(
            onClick = {
                pickSingleImage.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .build(),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("PickVisualMedia（仅图片）")
        }
        singleImageUri?.let { uri ->
            UriSummary(uri = uri, mime = context.contentResolver.getType(uri))
            AsyncImage(
                model = uri,
                contentDescription = "单选预览",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("多张图片（最多 5）", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        FilledTonalButton(
            onClick = {
                pickMultipleImages.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .setMaxItems(5)
                        .build(),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("PickMultipleVisualMedia")
        }
        if (multiImageUris.isNotEmpty()) {
            Text(
                "已选 ${multiImageUris.size} 张",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(
                    items = multiImageUris,
                    key = { it.toString() },
                ) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "多选缩略",
                        modifier = Modifier.size(96.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("文件（文档选择器）", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        FilledTonalButton(
            onClick = {
                openDocument.launch(
                    arrayOf(
                        "image/*",
                        "video/*",
                        "application/pdf",
                        "text/plain",
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("OpenDocument（图/视频/PDF/文本）")
        }
        documentUri?.let { uri ->
            UriSummary(uri = uri, mime = lastMime)
            if (lastMime?.startsWith("image/") == true) {
                AsyncImage(
                    model = uri,
                    contentDescription = "文档预览（图）",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UriSummary(uri: Uri, mime: String?) {
    Text(
        text = "Uri: $uri",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp),
    )
    Text(
        text = "MIME: ${mime ?: "（未知）"}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
