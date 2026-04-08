package com.xueh.commonlib.ui.compose

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * 相册与文件选择：`PickVisualMedia` / `PickMultipleVisualMedia` 走系统 Photo Picker；
 * `OpenDocument` 选通用文件；拍照使用 [ActivityResultContracts.TakePicture] 与 MediaStore / FileProvider 输出 Uri。
 */
@Composable
fun PhotoFilePickerDemoScreen() {
    val context = LocalContext.current
    var singleImageUri by remember { mutableStateOf<Uri?>(null) }
    var multiImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var documentUri by remember { mutableStateOf<Uri?>(null) }
    var lastMime by remember { mutableStateOf<String?>(null) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            pendingCaptureUri?.let { cameraUri = it }
        }
        pendingCaptureUri = null
    }
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val out = createCaptureImageUri(context)
            if (out == null) {
                ToastUtils.showShort("无法创建拍照输出 Uri")
            } else {
                pendingCaptureUri = out
                takePicture.launch(out)
            }
        } else {
            ToastUtils.showShort("需要相机权限才能拍照演示")
        }
    }

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

    fun launchCamera() {
        val out = createCaptureImageUri(context)
        if (out == null) {
            ToastUtils.showShort("无法创建拍照输出 Uri")
            return
        }
        pendingCaptureUri = out
        takePicture.launch(out)
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
            text = "单图/多图使用 Photo Picker；「文件」使用 OpenDocument。拍照使用官方 TakePicture 契约：Android 10+ 写入 MediaStore DCIM，低版本使用 UtilCode FileProvider 缓存路径。",
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("拍照（TakePicture）", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Text(
            text = "依赖 CAMERA 权限。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FilledTonalButton(
            onClick = {
                val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                if (ok) {
                    launchCamera()
                } else {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("打开相机拍照")
        }
        cameraUri?.let { uri ->
            UriSummary(uri = uri, mime = context.contentResolver.getType(uri))
            AsyncImage(
                model = uri,
                contentDescription = "拍照结果",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )
        }

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

/** 为 [ActivityResultContracts.TakePicture] 准备可写入的 content [Uri]。 */
private fun createCaptureImageUri(context: Context): Uri? {
    val mimeType = "image/jpeg"
    val fileName = "${System.currentTimeMillis()}.jpg"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    } else {
        val cacheDir = context.externalCacheDir ?: return null
        val authorities = "${Utils.getApp().packageName}.utilcode.provider"
        val file = File(cacheDir.absolutePath, fileName)
        FileProvider.getUriForFile(context, authorities, file)
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
